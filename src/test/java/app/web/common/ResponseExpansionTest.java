package app.web.common;

import app.web.common.response.expansion.Compact;
import app.web.common.response.expansion.Expandable;
import app.web.common.response.expansion.ResponseExpansion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;
import org.mockito.internal.matchers.Not;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author ngnmhieu
 * @since 03.06.16
 */
public class ResponseExpansionTest
{
    ObjectWriter ow = new ObjectMapper().writerWithDefaultPrettyPrinter();

    public static class Person
    {
        public String id;
        public String name;
        public int age;
        public Address address;

        public Person(String id, String name, int age, Address address)
        {
            this.id = id;
            this.name = name;
            this.age = age;
            this.address = address;
        }
    }

    @Expandable
    public static class Address
    {
        public String city;

        @Compact
        public Country country;

        public Address(String city, Country country)
        {
            this.city = city;
            this.country = country;
        }
    }

    @Expandable
    public static class Country
    {
        public String name;

        @Compact
        public String abbr;

        @Compact
        public Region region;

        public Country(String name, String abbr, Region region)
        {
            this.name = name;
            this.abbr = abbr;
            this.region = region;
        }
    }

    @Expandable
    public static class Region
    {
        @Compact
        public String name;

        public Region(String name)
        {
            this.name = name;
        }
    }

    //@Test
    //public void expand() throws Exception
    //{
    //    String[] expansionNodes = new String[]{"address"};
    //    Map<String, Object> result = new ResponseExpansion(expansionNodes)
    //            .expand(new Person("123", "Joe", 20,
    //                    new Address("Hamburg", new Country("Germany", "GER", new Region("nord")))));
    //
    //    System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(result));
    //
    //    assertEquals("Joe", result.get("name"));
    //    assertTrue(result.get("address") instanceof Map);
    //    result = (Map<String, Object>) result.get("address");
    //    assertTrue(result.get("country") instanceof Map);
    //    result = (Map<String, Object>) result.get("country");
    //    assertNull(result.get("name"));
    //    assertEquals("GER", result.get("abbr"));
    //}
    //
    //@Test
    //public void expandCompact() throws Exception
    //{
    //    String[] expansionNodes = new String[]{"address"};
    //    Map<String, Object> result = new ResponseExpansion(expansionNodes)
    //            .expand(new Person("123", "Joe", 20,
    //                    new Address("Hamburg", new Country("Germany", "GER", new Region("nord")))));
    //    assertEquals("nord", ((Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) result.get("address")).get("country")).get("region")).get("name"));
    //}
    //

    @Expandable
    public static class B
    {
        @Compact
        public int id = 2;

        public A attributeA;

        public B setAttributeA(A attributeA)
        {
            this.attributeA = attributeA;
            return this;
        }
    }

    @Expandable
    public static class A
    {
        @Compact
        public int id = 1;

        public B attributeB;

        public A(B b)
        {
            this.attributeB = b;
            if (b != null)
                b.setAttributeA(this);
        }
    }

    @Expandable
    public static class D
    {
        @Compact
        public int id = 4;
        public String expandedValue = "This value appears only in expand view.";
        public G attributeG = new G();
    }

    @Expandable
    public static class G
    {
        @Compact
        public int id = 6;
        public String expandedValue = "This value appears only in expand view.";
    }

    @Expandable
    public static class C
    {
        public int id = 3;
        public D[] arrayDs = new D[]{new D(), new D(), new D()};
    }

    @Test
    public void testExpand() throws Exception
    {
        B bObj = new B();
        A aObj = new A(bObj);
        Map<String, Object> tmp;

        Map<String, Object> compact = (Map<String, Object>) ResponseExpansion.expand(aObj, new String[]{""});
        tmp = (Map<String, Object>) compact.get("attributeB");
        assertNull(tmp.get("attributeA"));

        Map<String, Object> expandedB = (Map<String, Object>) ResponseExpansion.expand(aObj, new String[]{"attributeB"});
        tmp = (Map<String, Object>) expandedB.get("attributeB");
        assertNotNull(tmp.get("attributeA"));

        // attributeA nested in attributeB should be in compact form
        tmp = (Map<String, Object>) tmp.get("attributeA");
        assertNotNull(tmp.get("id"));
        assertNull(tmp.get("attributeB"));

        System.out.println("Compact Version: ");
        System.out.println(ow.writeValueAsString(compact));
        System.out.println("AttributeB Expanded Version: ");
        System.out.println(ow.writeValueAsString(expandedB));
    }

    @Test
    public void testExpandNull() throws Exception
    {
        A aObj = new A(null);
        Map<String, Object> tmp;

        Map<String, Object> result = (Map<String, Object>) ResponseExpansion.expand(aObj, new String[]{"attributeB"});
        assertNull(result.get("attributeB"));

        System.out.println("AttributeB is null");
        System.out.println(ow.writeValueAsString(result));

        result = (Map<String, Object>) ResponseExpansion.expand(null, new String[]{"attributeB"});
        assertNull(result);
    }

    @Test
    public void expandMapAsRoot() throws Exception
    {
        B bObj = new B();
        A aObj = new A(bObj);
        Map<String, Object> tmp;

        Map<String, A> map = new HashMap<>();
        map.put("attributeA", aObj);

        String[] rules = new String[]{"attributeA"};
        Map<String, Object> result = (Map<String, Object>) ResponseExpansion.expand(map, rules);
        tmp = (Map<String, Object>) result.get("attributeA");
        // attributeA should be expanded
        assertNotNull(tmp.get("id"));
        assertNotNull(tmp.get("attributeB"));
    }

    @Expandable
    public static class E
    {
        public int id = 5;
        public Map<String, D> mapD;

        public E()
        {
            mapD = new HashMap<>();
            mapD.put("attribute1", new D());
            mapD.put("attribute2", new D());
        }
    }

    @Test
    public void expandMapAsAttribute() throws Exception
    {
        String[] rules = new String[]{"mapD.attribute1"};

        Map<String, Object> result = (Map<String, Object>) ResponseExpansion.expand(new E(), rules);
        System.out.println("Expand mapDs:");
        System.out.println(ow.writeValueAsString(result));

        Map<String, Object> tmp = (Map<String, Object>) result.get("mapD");
        tmp = (Map<String, Object>) tmp.get("attribute1");
        assertNotNull(tmp.get("expandedValue"));


    }

    @Test
    public void expandArrayAsRoot() throws Exception
    {
        String[] rules = new String[]{};
        List compact = (List) ResponseExpansion.expand(new D[]{new D(), new D()}, rules);
        System.out.println("Compact array of G as root:");
        System.out.println(ow.writeValueAsString(compact));

        Map<String, Object> mapD = (Map<String, Object>) compact.get(0);
        assertNotNull(mapD.get("id"));
        assertNotNull(mapD.get("expandedValue"));
        Map<String, Object> mapG = (Map<String, Object>) mapD.get("attributeG");
        assertNotNull(mapG.get("id"));
        assertNull(mapG.get("expandedValue"));
    }

    @Test
    public void expandArrayAsAttribute() throws Exception
    {
        C cObj = new C();

        String[] expansionNodes = new String[]{""};
        // compact
        Map<String, Object> compact = (Map<String, Object>) ResponseExpansion.expand(cObj, expansionNodes);
        System.out.println("Compact arrayDs:");
        System.out.println(ow.writeValueAsString(compact));

        List ds = (List) compact.get("arrayDs");
        Map<String, Object> obj1 = ((Map<String, Object>) ds.get(0));
        assertNotNull(obj1.get("id"));
        assertNull(obj1.get("expandedValue"));

        // expanded
        expansionNodes = new String[]{"arrayDs"};
        Map<String, Object> expanded = (Map<String, Object>) ResponseExpansion.expand(cObj, expansionNodes);
        System.out.println("expanded arrayDs without expanding attributeG:");
        System.out.println(ow.writeValueAsString(expanded));

        ds = (List) expanded.get("arrayDs");
        obj1 = ((Map<String, Object>) ds.get(0));
        assertNotNull(obj1.get("expandedValue"));
        Map<String, Object> objG = (Map<String, Object>) obj1.get("attributeG");
        assertNull(objG.get("expandedValue"));

        // expand attributes of array element
        expansionNodes = new String[]{"arrayDs.attributeG"};
        expanded = (Map<String, Object>) ResponseExpansion.expand(cObj, expansionNodes);
        System.out.println("expanded arrayDs and attributeG:");
        System.out.println(ow.writeValueAsString(expanded));

        ds = (List) expanded.get("arrayDs");
        obj1 = ((Map<String, Object>) ds.get(0));
        objG = (Map<String, Object>) obj1.get("attributeG");
        assertNotNull(objG.get("expandedValue"));
    }


    @Expandable
    public static class X
    {
        public int id = 7;
        public Collection<Y> collectionY;

        public X()
        {
            collectionY = new HashSet();
            collectionY.add(new Y());
            collectionY.add(new Y());
        }
    }

    @Expandable
    public static class Y
    {
        @Compact
        public int id = 8;
        public String expandedValue = "This value appears only in expand view.";
    }

    @Test
    public void expandCollectionAsAttribute() throws Exception
    {
        X xObj = new X();

        String[] expansionNodes = new String[]{""};
        // compact
        Map<String, Object> compact = (Map<String, Object>) ResponseExpansion.expand(xObj, expansionNodes);
        System.out.println("Compact collectionY:");
        System.out.println(ow.writeValueAsString(compact));

        Collection collectionY = (Collection) compact.get("collectionY");
        Map<String, Object> objY = ((Map<String, Object>) collectionY.toArray()[0]);
        assertNotNull(objY.get("id"));
        assertNull(objY.get("expandedValue"));

        // expanded
        expansionNodes = new String[]{"collectionY"};
        Map<String, Object> expanded = (Map<String, Object>) ResponseExpansion.expand(xObj, expansionNodes);
        System.out.println("expanded collectionY:");
        System.out.println(ow.writeValueAsString(expanded));

        collectionY = (Collection) expanded.get("collectionY");
        objY = ((Map<String, Object>) collectionY.toArray()[0]);
        assertNotNull(objY.get("id"));
        assertNotNull(objY.get("expandedValue"));
    }

    static class NotExpandable
    {
        public int id = 9;
    }

    @Test
    public void notExpandObjectWithoutExpandableAnnotation() throws Exception
    {
        NotExpandable obj = new NotExpandable();
        assertEquals(obj, ResponseExpansion.expand(obj, new String[0]));
    }
}