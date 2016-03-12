package app.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ngnmhieu
 */
@RunWith(MockitoJUnitRunner.class)
public class TicketSetRepositoryTest
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    EntityManager em;

    private TicketSetRepository ticketSetRepository;

    @Before
    public void setUp() throws Exception
    {
        ticketSetRepository = new TicketSetRepository(em);
    }

    @Test
    public void findById_shouldReturnAnTicketSetOrNull() throws Exception
    {
        TicketSet mockTicketSet = mock(TicketSet.class);

        when(em.find(TicketSet.class, 123l)).thenReturn(mockTicketSet);
        assertEquals(mockTicketSet, ticketSetRepository.findById(123l));

        when(em.find(TicketSet.class, 123l)).thenReturn(null);
        assertNull(ticketSetRepository.findById(123l));
    }

    @Test
    public void findByIdAndUserIdAndEventId_shouldReturnTicketSetOrNull()
    {
        TicketSet mockTicketSet = mock(TicketSet.class);
        Query mockQuery = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);

        when(mockQuery.getSingleResult()).thenReturn(mockTicketSet);
        assertEquals(mockTicketSet, ticketSetRepository.findByIdAndUserIdAndEventId(1l, 123l, 10l));

        when(mockQuery.getSingleResult()).thenThrow(NoResultException.class);
        assertNull(ticketSetRepository.findByIdAndUserIdAndEventId(1l, 123l, 10l));
    }

    @Test
    public void saveOrUpdate_ShouldCreateNewTicketSet() throws Exception
    {
        TicketSet mockTicketSet = mock(TicketSet.class);
        when(mockTicketSet.getId()).thenReturn(null);
        assertEquals(mockTicketSet, ticketSetRepository.saveOrUpdate(mockTicketSet));
        verify(em, times(1)).persist(mockTicketSet);
    }

    @Test
    public void saveOrUpdate_shouldUpdateExistingTicketSet() throws Exception
    {
        TicketSet inputTicketSet = mock(TicketSet.class);
        TicketSet managedTicketSet = mock(TicketSet.class);
        when(em.merge(inputTicketSet)).thenReturn(managedTicketSet);

        assertEquals(managedTicketSet, ticketSetRepository.saveOrUpdate(inputTicketSet));
        verify(em, times(1)).merge(inputTicketSet);
    }

    @Test
    public void delete_shouldDeleteTicketSet() throws Exception
    {
        TicketSet mockTicketSet = mock(TicketSet.class);

        when(em.merge(mockTicketSet)).thenReturn(mockTicketSet);
        when(mockTicketSet.getEvent()).thenReturn(mock(Event.class));

        ticketSetRepository.delete(mockTicketSet);

        verify(em, times(1)).remove(mockTicketSet);
    }
}