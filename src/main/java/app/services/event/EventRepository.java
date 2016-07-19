package app.services.event;

import app.data.event.Event;
import app.services.BaseRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author ngnmhieu
 */
@Repository
public class EventRepository extends BaseRepository<Event, Long>
{
    public EventRepository()
    {
    }

    public EventRepository(EntityManager em)
    {
        super(em);
    }

    /**
     * @return all public events which haven't happened yet
     */
    public List<Event> findPublicEvents()
    {
        Query query = em.createQuery(
                "SELECT e FROM Event e WHERE e.canceled = false AND e.isPublic = true AND e.endTime > :now"
        ).setParameter("now", ZonedDateTime.now());

        return query.getResultList();
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

    @Override
    protected Class getEntityClass()
    {
        return Event.class;
    }
}
