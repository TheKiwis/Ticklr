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
     *
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
     * Create a new event or update an existing one
     * @param event
     */
    public Event saveOrUpdate(Event event)
    {
        // EntityManager#persist is called on new or managed entity,
        if (event.getId() == null || em.contains(event)) {
            em.persist(event);
        } else { // event is a detached entity (em.contains returns false)
            event = em.merge(event);
        }

        em.flush();

        return event;
    }

    /**
     * Finds an event by it's ID
     *
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

    public Event update(Event event)
    {
        return em.merge(event);
    }
}
