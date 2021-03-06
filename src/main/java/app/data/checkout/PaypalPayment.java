package app.data.checkout;

import app.data.basket.Basket;
import io.jsonwebtoken.lang.Assert;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 12.06.16
 */
@Entity
@Table(name = "paypal_payments")
public class PaypalPayment
{
    @Id
    protected Long id;

    @Column(name = "payment_id")
    protected String paymentId;

    @Column(name = "created_time")
    protected ZonedDateTime createdTime;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    protected Basket basket;

    protected PaypalPayment()
    {
    }

    /**
     * @param paymentId
     * @param basket
     */
    public PaypalPayment(String paymentId, Basket basket)
    {
        this.id = basket.getId();
        this.paymentId = paymentId;
        this.basket = basket;
        this.createdTime = ZonedDateTime.now();
    }

    /**
     * @return ID of the payment in system
     */
    public Long getId()
    {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return Time when the payment is created in system
     */
    public ZonedDateTime getCreatedTime()
    {
        return createdTime;
    }

    /**
     * @param createdTime
     */
    public void setCreatedTime(ZonedDateTime createdTime)
    {
        this.createdTime = createdTime;
    }

    /**
     * @return Paypal PaymentId
     */
    public String getPaymentId()
    {
        return paymentId;
    }

    /**
     * @param paymentId
     */
    public void setPaymentId(String paymentId)
    {
        Assert.notNull(paymentId);
        this.paymentId = paymentId;
    }

    @PreUpdate
    @PrePersist
    protected void initCreatedTime()
    {
        createdTime = ZonedDateTime.now();
    }
}
