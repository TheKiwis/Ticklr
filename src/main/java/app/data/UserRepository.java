package app.data;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.UUID;

/**
 * @author ngnmhieu
 */
@Repository
@Transactional
public class UserRepository
{
    @PersistenceContext
    private EntityManager em;

    public UserRepository(EntityManager em)
    {
        this.em = em;
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
        try {
            return em.find(User.class, userId);
        } catch (NoResultException e) {
            return null;
        }
    }


    /**
     * Finds an user by the given email
     *
     * @param email search for by given email
     * @return null if no user with the given email found
     */
    public User findByEmail(String email)
    {
        Query query = em.createQuery("SELECT u FROM User u WHERE u.email=:email").setParameter("email", email);

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
        em.persist(user);
        em.flush();
        return user;
    }
}
