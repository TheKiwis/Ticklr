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

    public User findById(int id)
    {
        return em.find(User.class, id);
    }

    public User findByEmail(String email)
    {
        Query query = em.createQuery("SELECT u FROM User u WHERE u.email=:email").setParameter("email", email);
        return (User) query.getSingleResult();
    }
}
