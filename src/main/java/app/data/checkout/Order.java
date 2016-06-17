package app.data.checkout;

import app.data.user.Buyer;
import io.jsonwebtoken.lang.Assert;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @author ngnmhieu
 * @since 09.06.16
 */
@Entity
@Table(name = "orders")
public class Order
{
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;

    @Column(name = "order_time")
    private ZonedDateTime orderTime;

    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.MERGE)
    private List<OrderPosition> orderPositions;

    protected Order()
    {
    }

    /**
     * @param orderTime != null
     * @param paymentMethod != null
     * @param buyer != null
     */
    public Order(ZonedDateTime orderTime, PaymentMethod paymentMethod, Buyer buyer)
    {
        Assert.notNull(orderTime);
        Assert.notNull(buyer);
        Assert.notNull(paymentMethod);

        this.orderTime = orderTime;
        this.paymentMethod = paymentMethod;
        this.buyer = buyer;
        this.orderPositions = new ArrayList<>();
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

    public Buyer getBuyer()
    {
        return buyer;
    }

    public void setBuyer(Buyer buyer)
    {
        this.buyer = buyer;
    }

    public List<OrderPosition> getOrderPositions()
    {
        return Collections.unmodifiableList(orderPositions);
    }

    public void addOrderPosition(OrderPosition orderPosition)
    {
        this.orderPositions.add(orderPosition);
        orderPosition.setOrder(this);
    }

    /**
     * @return Payment method that buyer have chosen
     */
    public PaymentMethod getPaymentMethod()
    {
        return paymentMethod;
    }

    /**
     * @return Payment method that buyer have chosen
     */
    public void setPaymentMethod(PaymentMethod paymentMethod)
    {
        this.paymentMethod = paymentMethod;
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