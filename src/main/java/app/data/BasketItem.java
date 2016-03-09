package app.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by DucNguyenMinh on 05.03.16.
 */

@Entity
@Table(name = "basket_items")
public class BasketItem
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ManyToOne
    @JoinColumn(name = "basket_id", nullable = false)
    protected Basket basket;

    @ManyToOne
    @JoinColumn(name = "ticket_set_id")
    protected TicketSet ticketSet;

    @Column(name = "quantity")
    protected Integer quantity;

    @Column(name = "unit_price")
    protected BigDecimal unitPrice;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time", updatable = false)
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    protected Date created_time;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_time", insertable = false, updatable = false)
    @Generated(GenerationTime.ALWAYS)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    protected Date updated_time;

    protected BasketItem()
    {
    }

    public BasketItem(Basket basket, Integer quantity, BigDecimal unitPrice)
    {
        this.basket = basket;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * @return BasketItem's ID
     */
    public String getId()
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

    public Date getCreatedTime()
    {
        return created_time;
    }

    public Date getUpdatedTime()
    {
        return updated_time;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasketItem that = (BasketItem) o;

        if (basket != null ? !basket.equals(that.basket) : that.basket != null) return false;
        if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null) return false;
        if (unitPrice != null ? !unitPrice.equals(that.unitPrice) : that.unitPrice != null) return false;
        if (created_time != null ? !created_time.equals(that.created_time) : that.created_time != null) return false;
        return updated_time != null ? updated_time.equals(that.updated_time) : that.updated_time == null;

    }

    @Override
    public int hashCode()
    {
        int result = basket != null ? basket.hashCode() : 0;
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (unitPrice != null ? unitPrice.hashCode() : 0);
        result = 31 * result + (created_time != null ? created_time.hashCode() : 0);
        result = 31 * result + (updated_time != null ? updated_time.hashCode() : 0);
        return result;
    }
}
