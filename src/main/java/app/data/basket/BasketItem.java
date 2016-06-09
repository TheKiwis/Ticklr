package app.data.basket;

import app.data.event.TicketSet;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author DucNguyenMinh
 * @since 05.03.16
 */

@Entity
@Table(name = "basket_items")
public class BasketItem
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "basket_id", nullable = false)
    protected Basket basket;

    @ManyToOne
    @JoinColumn(name = "ticket_set_id", nullable = false)
    protected TicketSet ticketSet;

    @Column(name = "quantity")
    protected Integer quantity;

    @Column(name = "unit_price")
    protected BigDecimal unitPrice;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time", updatable = false)
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    protected Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_time", insertable = false, updatable = false)
    @Generated(GenerationTime.ALWAYS)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    protected Date updatedTime;

    protected BasketItem()
    {
    }

    /**
     * @param ticketSet
     * @param quantity
     * @param unitPrice
     * @throws IllegalArgumentException if ticketSet == null || quantity < 0|| unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0
     */
    public BasketItem(TicketSet ticketSet, int quantity, BigDecimal unitPrice)
    {
        Assert.notNull(ticketSet);
        Assert.isTrue(quantity > 0);
        Assert.notNull(unitPrice);
        Assert.isTrue(unitPrice.compareTo(BigDecimal.ZERO) >= 0);

        this.ticketSet = ticketSet;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * @return BasketItem's ID
     */
    public Long getId()
    {
        return id;
    }

    /**
     * @return the basket contains this item
     */
    @JsonIgnore
    public Basket getBasket()
    {
        return basket;
    }

    /**
     * @param basket the basket contains this item
     */
    public void setBasket(Basket basket)
    {
        this.basket = basket;
    }

    public void setTicketSet(TicketSet ticketSet)
    {
        this.ticketSet = ticketSet;
    }

    public TicketSet getTicketSet()
    {
        return ticketSet;
    }

    /**
     * @return amount of the associated ticket set
     */
    public Integer getQuantity()
    {
        return quantity;
    }

    /**
     * @param quantity amount of the associated ticket set
     */
    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }

    /**
     * @return unit price of this item
     */
    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    /**
     * @param unitPrice unit price of this item
     */
    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    /**
     * @return total price of this item
     */
    public BigDecimal getTotalPrice()
    {
        return unitPrice.multiply(new BigDecimal(quantity));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasketItem that = (BasketItem) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return ticketSet != null ? ticketSet.equals(that.ticketSet) : that.ticketSet == null;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}
