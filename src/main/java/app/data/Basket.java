package app.data;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

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

    @OneToMany(mappedBy = "basket", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    protected Collection<BasketItem> items = new ArrayList<>();

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
     */
    public void addItem(BasketItem item)
    {
        items.add(item);
        item.basket = this;
    }

    /**
     * @param item BasketItem to be removed from the Basket
     *             the first
     */
    public void removeItem(BasketItem item)
    {
        items.remove(item);
    }
}
