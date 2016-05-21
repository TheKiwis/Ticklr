package app.services;

import app.data.Identity;
import app.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.UUID;

/**
 * Manages persistent User objects
 *
 * @author ngnmhieu
 */
@Repository
@Transactional
public class UserRepository
{
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private IdentityRepository identityRepository;

    /**
     * @param em
     * @param identityRepository manages persistent Identity objects
     */
    public UserRepository(EntityManager em, IdentityRepository identityRepository)
    {
        this.em = em;
        this.identityRepository = identityRepository;
    }

    private UserRepository()
    {
    }

    /**
     * Finds an user by the given ID
     *
     * @param userId the given user's ID
     * @return null if no user found
     */
    public User findById(UUID userId)
    {
        return em.find(User.class, userId);
    }


    /**
     * Finds an user by the given email
     *
     * @param email search for by given email
     * @return null if no user with the given email found
     */
    public User findByEmail(String email)
    {
        Query query = em.createQuery("SELECT u FROM User u WHERE u.identity.email=:email").setParameter("email", email);

        User user = null;
        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
        }

        return user;
    }

    /**
     * Saves user to the database
     *
     * @param user
     * @return
     * @throws PersistenceException if user already existed (email should be unique)
     */
    public User save(User user) throws PersistenceException
    {
        // first save user's identity
        Identity id = identityRepository.save(user.getIdentity());

        user.setIdentity(id);

        em.persist(user);

        em.flush();

        return user;
    }
}
