package app.services;

import app.data.event.Event;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

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
     *
     * @param event
     */
    public Event saveOrUpdate(Event event)
    {
        if (event.getId() == null) {
            em.persist(event);
        } else {
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
    public Event findById(Long eventId)
    {
        try {
            return em.find(Event.class, eventId);
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Finds all the events of the user corresponding to userId
     * @param userId
     * @return
     */
    public List<Event> findByUserId(UUID userId)
    {
        return em.createQuery("SELECT e FROM Event e WHERE e.user.id = :userId")
                .setParameter("userId", userId)
                .getResultList();
    }
}
