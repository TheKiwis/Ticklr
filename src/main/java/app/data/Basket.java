package app.data;

import com.fasterxml.jackson.annotation.JsonFormat;
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
 * Created by DucNguyenMinh on 05.03.16.
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
    @JoinColumn(name = "user_id")
    protected User user;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time", updatable = false)
    @CreationTimestamp
    protected Date created_time;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_time", insertable = false, updatable = false)
    @Generated(GenerationTime.ALWAYS)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    protected Date updated_time;

    @OneToMany(mappedBy = "basket", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    protected Collection<BasketItem> basketItems = new ArrayList<>();

    protected Basket()
    {
    }

    public Basket(User user)
    {
        this.user = user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Long getId()
    {
        return id;
    }

    public User getUser()
    {
        return user;
    }

    public Date getCreatedTime()
    {
        return created_time;
    }

    public Date getUpdatedTime()
    {
        return updated_time;
    }

    /**
     * @return Items contained in this Basket
     */
    public Collection<BasketItem> getBasketItems()
    {
        return Collections.unmodifiableCollection(basketItems);
    }

    /**
     * @param item to be added to the basket
     */
    public void addItem(BasketItem item)
    {
        basketItems.add(item);
        item.basket = this;
    }
}
