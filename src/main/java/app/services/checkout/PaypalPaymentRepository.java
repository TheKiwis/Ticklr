package app.services.checkout;

import app.data.checkout.PaypalPayment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author ngnmhieu
 * @since 12.06.16
 */
@Repository
@Transactional
public class PaypalPaymentRepository
{
    @PersistenceContext
    private EntityManager em;

    /**
     * Store the payment into the database
     *
     * @param payment
     * @return
     */
    public PaypalPayment save(PaypalPayment payment)
    {
        return em.merge(payment);
    }

    /**
     * Removes a PaypalPayment object if it exists
     *
     * @param id ID of the paypal payment object
     */
    public void remove(Long id)
    {
        em.createQuery("DELETE FROM PaypalPayment p WHERE p.id = :id")
                .setParameter("id", id).executeUpdate();
    }

    /**
     * @param id Payment ID in system
     */
    public PaypalPayment find(Long id)
    {
       return em.find(PaypalPayment.class, id);
    }
}
