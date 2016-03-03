package app.data;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * @author ngnmhieu
 */
@Repository
@Transactional
public class EventRepository
{
    @PersistenceContext
    private EntityManager em;

    /**
     * Construct an instance of EventRepository
     * @param em manages ORM Entities
     */
    public EventRepository(EntityManager em)
    {
        this.em = em;
    }

    private EventRepository()
    {
    }

    /**
     * Create a new event
     * @param event
     */
    public Event save(Event event)
    {
        em.persist(event);
        return event;
    }

    /**
     * Finds an event by it's ID
     * @param eventId
     * @return null if no event is found
     */
    public Event findById(long eventId)
    {
        try {
            return em.find(Event.class, eventId);
        } catch (NoResultException e) {
            return null;
        }
    }
}
