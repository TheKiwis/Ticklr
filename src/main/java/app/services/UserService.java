package app.services;

import app.data.user.Identity;
import app.data.user.User;
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
public class UserService
{
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private IdentityService identityService;

    /**
     * @param em
     * @param identityService manages persistent Identity objects
     */
    public UserService(EntityManager em, IdentityService identityService)
    {
        this.em = em;
        this.identityService = identityService;
    }

    private UserService()
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
     * Finds an user by the given identity
     *
     * @param id
     * @return
     */
    public User findByIdentity(Identity id)
    {
        Query query = em.createQuery("SELECT u FROM User u WHERE u.identity = :identity").setParameter("identity", id);

        User user = null;
        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
        }

        return user;
    }

    /**
     * @param id
     * @return a newly created user, who has the given identity
     *
     * @ensure user.getIdentity().equals(id)
     */
    public User createWithIdentity(Identity id)
    {
        User user = new User(id);

        em.persist(user);

        return user;
    }
}
