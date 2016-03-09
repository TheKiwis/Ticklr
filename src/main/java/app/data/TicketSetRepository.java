package app.data;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author ngnmhieu
 */
@Repository
@Transactional
public class TicketSetRepository
{
    @PersistenceContext
    private EntityManager em;

    public TicketSetRepository(EntityManager em)
    {
        this.em = em;
    }

    protected TicketSetRepository()
    {
    }

    public TicketSet findById(Long id)
    {
        return em.find(TicketSet.class, id);
    }
}
