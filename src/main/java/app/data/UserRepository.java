package app.data;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author ngnmhieu
 */
public class UserRepository
{
    private EntityManager em;

    public UserRepository(EntityManager em)
    {
        this.em = em;
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
    public User findByEmail(String email)
    {
        Query query = em.createQuery("SELECT u FROM User u WHERE u.email=:email").setParameter("email", email);
        return (User) query.getSingleResult();
    }
}
