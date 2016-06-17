package app.services.checkout;

import app.data.checkout.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 13.06.16
 */
@Repository
@Transactional
public class OrderRepository
{
    @PersistenceContext
    private EntityManager em;

    /**
     * Construct an instance of OrderRepository
     *
     * @param em manages ORM Entities
     */
    public OrderRepository(EntityManager em)
    {
        this.em = em;
    }

    protected OrderRepository()
    {
    }

    public Order save(Order order)
    {
        return em.merge(order);
    }

    /**
     * Finds an order by it's ID
     *
     * @param orderId
     * @return null if no order is found
     */
    public Order findById(UUID orderId)
    {
        return em.find(Order.class, orderId);
    }

}
