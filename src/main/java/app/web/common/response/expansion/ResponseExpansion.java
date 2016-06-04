package app.web.common.response.expansion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ngnmhieu
 * @since 03.06.16
 */
public class ResponseExpansion
{
    protected final Log logger = LogFactory.getLog(getClass());

    private ExpansionNode expansionRoot;

    private static class ExpansionNode
    {
        // set of child nodes
        Set<ExpansionNode> children;

        // associated value
        String value;

        /**
         * Initialize an ExpansionNode with the given value
         *
         * @param value
         */
        public ExpansionNode(String value)
        {
            this.value = value;
            this.children = new HashSet<>();
        }

        /**
         * @param value the value associated with an ExpansionNode
         * @return The direct child containing the given value, null if not found
         */
        public ExpansionNode getChildFor(String value)
        {
            Optional<ExpansionNode> op = this.children.stream()
                    .filter(child -> value.equals(child.getValue()))
                    .findAny();
            return op.isPresent() ? op.get() : null;
        }

        /**
         * Add a child with the gien value to this node
         *
         * @param value the value associated with an ExpansionNode
         * @throws IllegalArgumentException if getChildFor(value) != null
         * @require null == getChildFor(value)
         */
        public ExpansionNode addChild(String value)
        {
            if (getChildFor(value) != null)
                throw new IllegalArgumentException("Cannot add child node because " +
                        "one with the value '" + value + "' existed.");

            ExpansionNode child = new ExpansionNode(value);

            children.add(child);

            return child;
        }

        /**
         * @return the value associated with the node
         */
        public String getValue()
        {
            return value;
        }
    }

    private static class ObjectAttribute
    {
        private String name;
        private Class type;
        private Object value;

        public ObjectAttribute(String name, Class type, Object value)
        {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        public String getName()
        {
            return name;
        }

        public Class getType()
        {
            return type;
        }

        public Object getValue()
        {
            return value;
        }
    }

    /**
     * Instantiate a ResponseExpansion object with specific expansion rules.
     * These rules will be applied when object is being expanded.
     *
     * @param expansionRules e.g: ["person.address", "basket.items", ....]
     */
    public ResponseExpansion(String[] expansionRules)
    {
        if (expansionRules == null)
            expansionRules = new String[0];
        expansionRoot = buildExpansionTree(expansionRules);
    }

    /**
     * @param expansionRules
     * @return root of the created tree
     */
    private ExpansionNode buildExpansionTree(String[] expansionRules)
    {
        ExpansionNode root = new ExpansionNode("root");

        for (String rule : expansionRules) {
            List<String> path = Arrays.asList(rule.split("\\."));
            addNode(root, path);
        }

        return root;
    }

    /**
     * If any node on the path doesn't exist, it is created
     *
     * @param root root of the tree
     * @param path path to the node, e.g: user.address.country is the path to node country
     */
    private void addNode(ExpansionNode root, List<String> path)
    {
        if (path.isEmpty())
            return;

        String value = path.get(0);

        ExpansionNode child = root.getChildFor(value);

        if (child == null)
            child = root.addChild(value);

        addNode(child, path.subList(1, path.size()));
    }

    /**
     * @param obj
     * @return
     */
    public Object expand(Object obj)
    {
        return expand(obj, expansionRoot);
    }

    /**
     * @param obj
     * @param expansionRoot
     * @return
     */
    private Object expand(Object obj, ExpansionNode expansionRoot)
    {
        if (obj == null)
            return null;

        Class objClass = obj.getClass();

        // ignore objects that are not "expandable"
        if (!isExpandable(objClass)) {
            return obj;
        }

        boolean isCompact = expansionRoot == null;

        // If object is an Array or Collection, expands all elements
        // and return that "expanded" Array / Collection.
        if (objClass.isArray() || Collection.class.isAssignableFrom(objClass)) {

            Collection coll = objClass.isArray() ? Arrays.asList((Object[]) obj) : (Collection) obj;

            return coll.stream().map(val -> {
                if (val == null)
                    return null;

                Class klass = val.getClass();
                // TODO: It can only expands Expandable or Map objects directly inside a Collection, not in nested Collection
                if (klass.isAnnotationPresent(Expandable.class) || Map.class.isAssignableFrom(klass)) {
                    val = expand(val, expansionRoot);
                }

                return val;
            }).collect(Collectors.toList());
        }

        Map<String, Object> result = new HashMap<>();

        List<ObjectAttribute> attributes = null;

        if (obj instanceof Map) {
            // collect all key-value pairs in the map as attributes
            attributes = collectMapEntries((Map) obj);
        } else {
            // collect all public fields of the object as attributes
            attributes = collectFields(obj, isCompact);
        }

        attributes.forEach(attr -> {
            Object val = attr.getValue();

            ExpansionNode childNode = expansionRoot != null ? expansionRoot.getChildFor(attr.getName()) : null;

            // Expand the object in this field to a Map if its class is annotated with @Expandable
            Class klass = attr.getType();
            if (isExpandable(klass)) {
                val = expand(val, childNode);
            }

            result.put(attr.getName(), val);
        });

        return result;
    }

    /**
     * An object of a Class is expandable when it either is a Map,
     * an Array, a Collection or is annotated with @Expandable.
     *
     * @param klass
     * @return if objects of the given Class are "expandable" or not
     */
    private boolean isExpandable(Class klass)
    {
        return klass.isAnnotationPresent(Expandable.class)
                || Map.class.isAssignableFrom(klass)
                || klass.isArray()
                || Collection.class.isAssignableFrom(klass);
    }

    /**
     * @param map
     * @return ObjectAttributes collected from all map entries
     */
    private List<ObjectAttribute> collectMapEntries(Map<Object, Object> map)
    {
        return map.entrySet().stream()
                .map(entry -> new ObjectAttribute(entry.getKey().toString(), entry.getValue().getClass(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * @param obj       the object whose fields to be collected
     * @param isCompact should only the fields annotated with @Compact be returned
     * @return the attributes of the given object extracted from public fields
     */
    private List<ObjectAttribute> collectFields(Object obj, boolean isCompact)
    {
        Stream<Field> fields = Arrays.stream(obj.getClass().getDeclaredFields());

        Stream<Field> publicFields = fields.filter(field -> {
            field.setAccessible(true); // suppress Java language access checking
            return Modifier.isPublic(field.getModifiers());
        });

        // only attributes that are annotated with @Compact are included in compact mode
        if (isCompact) {
            publicFields = publicFields.filter(field -> field.isAnnotationPresent(Compact.class));
        }

        List<ObjectAttribute> attributes = publicFields.map(field -> {
            try {
                return new ObjectAttribute(field.getName(), field.getType(), field.get(obj));
            } catch (IllegalAccessException e) {
                logger.warn("field " + field.getName() + " is not accessible.");
                return null;
            }
        }).collect(Collectors.toList());

        return attributes;
    }

    /**
     * Alternative to new ResponseExpansion(expansionRules).expand(obj)
     *
     * @param obj
     * @param expansionRules
     * @return
     */

    public static Object expand(Object obj, String[] expansionRules)
    {
        return new ResponseExpansion(expansionRules).expand(obj);
    }

}
