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

    private TicketSetRepository repository;

    @Before
    public void setUp() throws Exception
    {
        repository = new TicketSetRepository(em);
    }

    @Test
    public void findById_shouldReturnAnTicketSetOrNull() throws Exception
    {
        TicketSet mockTicketSet = mock(TicketSet.class);

        when(em.find(TicketSet.class, 123l)).thenReturn(mockTicketSet);
        assertEquals(mockTicketSet, repository.findById(123l));

        when(em.find(TicketSet.class, 123l)).thenReturn(null);
        assertNull(repository.findById(123l));
    }

    @Test
    public void findByIdAndUserIdAndEventId_shouldReturnTicketSetOrNull()
    {
        TicketSet mockTicketSet = mock(TicketSet.class);
        Query mockQuery = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);

        when(mockQuery.getSingleResult()).thenReturn(mockTicketSet);
        assertEquals(mockTicketSet, repository.findByIdAndUserIdAndEventId(1l, 123l, 10l));

        when(mockQuery.getSingleResult()).thenThrow(NoResultException.class);
        assertNull(repository.findByIdAndUserIdAndEventId(1l, 123l, 10l));
    }

    @Test
    public void saveOrUpdate_ShouldCreateNewTicketSet() throws Exception
    {
        TicketSet mockTicketSet = mock(TicketSet.class);
        when(mockTicketSet.getId()).thenReturn(null);
        assertEquals(mockTicketSet, repository.saveOrUpdate(mockTicketSet));
        verify(em, times(1)).persist(mockTicketSet);
    }

    @Test
    public void saveOrUpdate_ShouldUpdateExistingTicketSet() throws Exception
    {
        TicketSet inputTicketSet = mock(TicketSet.class);
        TicketSet managedTicketSet = mock(TicketSet.class);
        when(em.merge(inputTicketSet)).thenReturn(managedTicketSet);

        assertEquals(managedTicketSet, repository.saveOrUpdate(inputTicketSet));
        verify(em, times(1)).merge(inputTicketSet);
    }
}