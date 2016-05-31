package app.data;

import org.hibernate.annotations.*;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;

/**
 * @author DucNguyenMinh
 * @since 05.03.16.
 */
@Entity
@Table(name = "baskets")
public class Basket
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "buyer_id")
    protected Buyer buyer;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time", updatable = false)
    @CreationTimestamp
    protected Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_time", insertable = false, updatable = false)
    @Generated(GenerationTime.ALWAYS)
    protected Date updatedTime;

    //@OneToMany(mappedBy = "basket", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @OneToMany(mappedBy = "basket", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    protected Set<BasketItem> items = new HashSet<>();

    protected Basket()
    {
    }

    public Basket(Buyer buyer)
    {
        this.buyer = buyer;
    }

    public Buyer getBuyer()
    {
        return buyer;
    }

    public void setBuyer(Buyer buyer)
    {
        this.buyer = buyer;
    }

    public Long getId()
    {
        return id;
    }

    /**
     * @return Items an immutable collection of BasketItem's contained in this Basket
     */
    public Collection<BasketItem> getItems()
    {
        return Collections.unmodifiableCollection(items);
    }

    /**
     * @param item BasketItem to be added to this Basket
     * @ensure true == #isInBasket(item)
     */
    public void addItem(BasketItem item)
    {
        items.add(item);
        item.basket = this;
    }

    /**
     * @param ticketSet
     * @return if there is a basket item corresponding to the given ticket set
     */
    public boolean isInBasket(TicketSet ticketSet)
    {
        return items.stream().anyMatch(item -> item.getTicketSet().equals(ticketSet));
    }

    /**
     * @param item
     * @return if there is a item in this basket
     */
    public boolean isInBasket(BasketItem item)
    {
        return items.stream().anyMatch(i -> i.equals(item));
    }

    /**
     * @param ticketSet
     * @return the BasketItem object corresponding to the given ticket set, null if nothing found
     * @require ticketSet != null
     */
    public BasketItem getItemFor(TicketSet ticketSet)
    {
        Assert.notNull(ticketSet);
        Optional<BasketItem> optional = items.stream().filter(item -> item.getTicketSet().equals(ticketSet)).findFirst();

        return optional.isPresent() ? optional.get() : null;
    }

    /**
     * @param item BasketItem to be removed from the Basket
     *             the first
     * @ensure false == #isInBasket(item)
     */
    public void removeItem(BasketItem item)
    {
        items.remove(item);
    }
}
