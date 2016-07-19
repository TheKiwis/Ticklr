package app.services;

import app.data.event.Event;
import app.services.event.EventRepository;
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
    public void findByUserId_shouldReturnEventList() throws Exception
    {
        List<Event> expectedList = new ArrayList();
        expectedList.add(new Event());
        expectedList.add(new Event());
        when(em.createQuery(anyString()).getResultList()).thenReturn(expectedList);
        assertFalse(eventRepository.findByUserId(UUID.randomUUID()).isEmpty());
    }
}