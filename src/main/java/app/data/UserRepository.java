package app.data;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
     * find an user by the given id
     *
     * @param id the given id
     * @return User who assigned to the given Id
     */
    public User findById(int id)
    {
        return em.find(User.class, id);
    }


    /**
     * find an user by the given email
     *
     * @param email search for by given email
     * @return 
     */
    // todo integration test
    public User findByEmail(String email)
    {
        Query query = em.createQuery("SELECT u FROM User u WHERE u.email=:email").setParameter("email", email);
        return (User) query.getSingleResult();
    }

    // todo test transaction fail
    public User save(User user)
    {
        em.persist(user);
        return user;
    }
}
