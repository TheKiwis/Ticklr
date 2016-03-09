package app.data;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

    /**
     * @param id of the ticket set
     * @return
     */
    public TicketSet findById(Long id)
    {
        return em.find(TicketSet.class, id);
    }

    /**
     * @param ticketSetId
     * @param userId
     * @param eventId
     * @return
     */
    public TicketSet findByIdAndUserIdAndEventId(long ticketSetId, long userId, long eventId)
    {
        try {
            return (TicketSet) em.createQuery("SELECT ts FROM TicketSet ts WHERE ts.id = :ticketSetId AND ts.event.id = :eventId AND ts.event.user.id = :userId")
                    .setParameter("ticketSetId", ticketSetId)
                    .setParameter("eventId", eventId)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param ticketSet to be saved
     * @return
     */
    public TicketSet saveOrUpdate(TicketSet ticketSet)
    {
        if (ticketSet.getId() == null || em.contains(ticketSet)) {
            em.persist(ticketSet);
        } else { // ticketSet is a detached entity (em.contains returns false)
            ticketSet = em.merge(ticketSet);
        }

        em.flush();

        return ticketSet;
    }
}
