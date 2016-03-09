package app.data;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

/**
 * Created by DucNguyenMinh on 08.03.16.
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
    public Basket findByUserId(Long userId)
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

    public Basket saveOrUpdate(Basket basket){

        if (basket.getId() == null) {
            em.persist(basket);
        } else {
            basket = em.merge(basket);
        }

        em.flush();

        return basket;
    }
}
