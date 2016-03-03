package app.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author ngnmhieu
 */
@RunWith(MockitoJUnitRunner.class)
public class EventRepositoryTest
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    EntityManager em;

    // test object
    private EventRepository eventRepository;

    @Before
    public void setUp() throws Exception
    {
        eventRepository = new EventRepository(em);
    }

    @Test
    public void save_ShouldCreateNewEvent() throws Exception
    {
        Event mockEvent = mock(Event.class);
        eventRepository.save(mockEvent);
        verify(em, times(1)).persist(mockEvent);
    }

    @Test
    public void findById_shouldReturnTheCorrectEvent() throws Exception
    {
        Event event = mock(Event.class);
        when(em.find(Event.class, 123l)).thenReturn(event);

        EventRepository repo = new EventRepository(em);
        assertEquals(event, repo.findById(123l));
    }

    @Test
    public void findById_shouldReturnNullIfNoEventFound() throws Exception
    {
        when(em.find(Event.class, 123l)
        ).thenThrow(NoResultException.class);

        EventRepository repo = new EventRepository(em);

        assertNull(repo.findById(123l));
    }
}