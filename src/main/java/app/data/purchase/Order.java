package app.data.purchase;

import app.data.user.Buyer;
import io.jsonwebtoken.lang.Assert;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 09.06.16
 */
@Entity(name = "orders")
public class Order
{
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;

    @Column(name = "order_time")
    private ZonedDateTime orderTime;

    @Column(name = "status")
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    @OneToMany
    private Set<OrderPosition> orderPositions;

    protected Order()
    {
    }

    /**
     * @param id != null
     * @param orderTime != null
     * @param status default to OrderStatus.PENDING
     * @param buyer != null
     */
    public Order(UUID id, ZonedDateTime orderTime, OrderStatus status, Buyer buyer)
    {
        Assert.notNull(id);
        Assert.notNull(orderTime);
        Assert.notNull(buyer);

        this.id = id;
        this.orderTime = orderTime;
        this.status = status == null ? OrderStatus.PENDING : status;
        this.buyer = buyer;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID orderId)
    {
        this.id = orderId;
    }

    public ZonedDateTime getOrderTime()
    {
        return orderTime;
    }

    public void setOrderTime(ZonedDateTime date)
    {
        this.orderTime = date;
    }

    public OrderStatus getStatus()
    {
        return status;
    }

    public void setStatus(OrderStatus status)
    {
        this.status = status;
    }

    public Buyer getBuyer()
    {
        return buyer;
    }

    public void setBuyer(Buyer buyer)
    {
        this.buyer = buyer;
    }

    public Set<OrderPosition> getOrderPositions()
    {
        return Collections.unmodifiableSet(orderPositions);
    }

    public void addOrderPosition(OrderPosition orderPosition)
    {
        this.orderPositions.add(orderPosition);
    }

    public static enum OrderStatus
    {
        PENDING, APPROVED, CANCELED
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return id != null ? id.equals(order.id) : order.id == null;

    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}