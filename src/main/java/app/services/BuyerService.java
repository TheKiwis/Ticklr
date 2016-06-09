package app.services;

import app.data.user.Buyer;
import app.data.user.Identity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.UUID;

/**
 * Manages persistent Buyer objects
 *
 * @author ngnmhieu
 */
@Repository
@Transactional
public class BuyerService
{
    @PersistenceContext
    private EntityManager em;

    /**
     * @param em
     */
    public BuyerService(EntityManager em)
    {
        this.em = em;
    }

    private BuyerService()
    {
    }

    /**
     * Finds an buyer by the given ID
     *
     * @param buyerId the given buyer's ID
     * @return null if no buyer found
     */
    public Buyer findById(UUID buyerId)
    {
        return em.find(Buyer.class, buyerId);
    }


    /**
     * Finds an user by the given identity
     * TODO Test
     *
     * @param id
     * @return
     */
    public Buyer findByIdentity(Identity id)
    {
        Query query = em.createQuery("SELECT b FROM Buyer b WHERE b.identity = :identity").setParameter("identity", id);

        Buyer buyer = null;
        try {
            buyer = (Buyer) query.getSingleResult();
        } catch (NoResultException e) {
        }

        return buyer;
    }

    /**
     * @param id
     * @return a newly created buyer, who has the given identity
     *
     * @ensure buyer.getIdentity().equals(id)
     */
    public Buyer createWithIdentity(Identity id)
    {
        Buyer buyer = new Buyer(id);

        em.persist(buyer);

        return buyer;
    }
}
