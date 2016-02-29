package app.data;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
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
    public User findByEmail(String email)
    {
        Query query = em.createQuery("SELECT u FROM User u WHERE u.email=:email").setParameter("email", email);
        // todo try catch return null
        return (User) query.getSingleResult();
    }

    /**
     * Save user to the database
     * @param user
     * @return
     * @throws PersistenceException if user already existed (email should be unique)
     */
    public void save(User user) throws PersistenceException
    {
        em.persist(user);
    }
}
