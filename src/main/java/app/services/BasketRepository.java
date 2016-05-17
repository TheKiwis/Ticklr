package app.services;

import app.data.Basket;
import app.data.BasketItem;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.UUID;

/**
 * @author DucNguyenMinh
 * @since 08.03.16
 */
@Repository
@Transactional
public class BasketRepository
{
    @PersistenceContext
    private EntityManager em;

    public BasketRepository(EntityManager em)
    {
        this.em = em;
    }

    protected BasketRepository()
    {
    }

    /**
     * @param userId ID of the user who owns the basket
     * @return null if nothing found
     */
    public Basket findByUserId(UUID userId)
    {
        Query query = em.createQuery("SELECT b FROM Basket b WHERE b.user.id = :user_id");

        query.setParameter("user_id", userId);

        Basket basket = null;

        try {
            basket = (Basket) query.getSingleResult();
        } catch (NoResultException e) {
        }

        return basket;
    }

    /**
     * @param basket
     * @return
     */
    public Basket save(Basket basket)
    {
        em.persist(basket);
        em.flush();
        return basket;
    }

    /**
     * @param basket
     * @return
     */
    public Basket saveOrUpdate(Basket basket)
    {
        if (basket.getId() == null) {
            em.persist(basket);
        } else {
            basket = em.merge(basket);
        }

        em.flush();

        return basket;
    }

    /**
     * Find item associated with the given ticket-set in the given basket
     *
     * @param basketId
     * @param ticketSetId
     * @return null if nothing found
     */
    public BasketItem findItemByBasketIdAndTicketSetId(Long basketId, Long ticketSetId)
    {
        Query query = em.createQuery("select i from BasketItem i where i.basket.id = :basketId and i.ticketSet.id = :ticketSetId")
                .setParameter("ticketSetId", ticketSetId)
                .setParameter("basketId", basketId);

        try {
            return (BasketItem) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param item != null
     */
    public BasketItem updateItem(BasketItem item)
    {
        return em.merge(item);
    }

    /**
     * @param item the basket item to be deleted
     */
    public void deleteItem(BasketItem item)
    {
        // make this entity managed if needed
        item = em.contains(item) ? item : em.merge(item);

        item.getBasket().removeItem(item);

        em.remove(item);
    }

    /**
     * @param itemId
     * @return the basket item with the given itemId
     */
    public BasketItem findItemById(long itemId)
    {
        Query query = em.createQuery("SELECT i FROM BasketItem i WHERE i.id = :itemId")
                .setParameter("itemId", itemId);

        try {
            return (BasketItem) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
