package app.services;

import app.data.Identity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 21.05.16
 */
@Repository
@Transactional
public class IdentityRepository
{
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public IdentityRepository(EntityManager em, PasswordEncoder passwordEncoder)
    {
        this.em = em;
        this.passwordEncoder = passwordEncoder;
    }

    private IdentityRepository()
    {
    }

    /**
     * Save an identity into the database
     *
     * @return newly created identity
     * @throws PersistenceException if the same identity exists in the database
     * @require identity != null
     */
    public Identity save(Identity identity)
    {
        if (identity.getId() == null) {
            identity.setPassword(passwordEncoder.encode(identity.getPassword()));
            em.persist(identity);
        } else {
            identity = em.merge(identity);
        }

        em.flush();

        return identity;
    }

    /**
     * @param uuid
     * @return the identity that whose id matches the given uuid
     *         null if no identity is found
     */
    public Identity findById(UUID uuid)
    {
        return em.find(Identity.class, uuid);
    }
}
