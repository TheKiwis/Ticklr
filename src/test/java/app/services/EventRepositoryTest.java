package app.services;

import app.data.Event;
import app.services.EventRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public void saveOrUpdate_ShouldCreateNewEvent() throws Exception
    {
        Event mockEvent = mock(Event.class);
        when(mockEvent.getId()).thenReturn(null);
        assertEquals(mockEvent, eventRepository.saveOrUpdate(mockEvent));
        verify(em, times(1)).persist(mockEvent);
    }

    @Test
    public void saveOrUpdate_ShouldUpdateExistingEvent() throws Exception
    {
        Event inputEvent = mock(Event.class);
        Event managedEvent = mock(Event.class);
        when(em.merge(inputEvent)).thenReturn(managedEvent);

        assertEquals(managedEvent, eventRepository.saveOrUpdate(inputEvent));
        verify(em, times(1)).merge(inputEvent);
    }

    @Test
    public void findById_shouldReturnTheCorrectEvent() throws Exception
    {
        Event event = mock(Event.class);
        when(em.find(Event.class, 123l)).thenReturn(event);

        assertEquals(event, eventRepository.findById(123l));
    }

    @Test
    public void findById_shouldReturnNullIfNoEventFound() throws Exception
    {
        when(em.find(Event.class, 123l)
        ).thenThrow(NoResultException.class);

        assertNull(eventRepository.findById(123l));
    }

    @Test
    public void findByUserId_shouldReturnEventList() throws Exception
    {
        UUID userId = UUID.randomUUID();
        List<Event> expectedList = new ArrayList();
        expectedList.add(new Event());
        expectedList.add(new Event());
        when(em.createQuery(anyString()).getResultList()).thenReturn(expectedList);
        assertFalse(eventRepository.findByUserId(UUID.randomUUID()).isEmpty());
    }
}