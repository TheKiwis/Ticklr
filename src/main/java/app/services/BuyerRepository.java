package app.services;

import app.data.Buyer;
import app.data.Identity;
import app.data.Buyer;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BuyerRepository
{
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private IdentityRepository identityRepository;

    /**
     * @param em
     * @param identityRepository manages persistent Identity objects
     */
    public BuyerRepository(EntityManager em, IdentityRepository identityRepository)
    {
        this.em = em;
        this.identityRepository = identityRepository;
    }

    private BuyerRepository()
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
     * Finds an buyer by the given email
     *
     * @param email search for by given email
     * @return null if no buyer with the given email found
     */
    public Buyer findByEmail(String email)
    {
        Query query = em.createQuery("SELECT u FROM Buyer u WHERE u.identity.email=:email").setParameter("email", email);

        Buyer buyer = null;
        try {
            buyer = (Buyer) query.getSingleResult();
        } catch (NoResultException e) {
        }

        return buyer;
    }

    /**
     * Saves buyer to the database
     *
     * @param buyer
     * @return
     * @throws PersistenceException if buyer already existed (email should be unique)
     */
    public Buyer save(Buyer buyer) throws PersistenceException
    {
        // first save buyer's identity
        Identity id = identityRepository.save(buyer.getIdentity());

        buyer.setIdentity(id);

        em.persist(buyer);

        em.flush();

        return buyer;
    }
}
