package app.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author ngnmhieu
 */
@RunWith(MockitoJUnitRunner.class)
public class TicketSetRepositoryTest
{
    @Mock
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
        assertEquals(null, repository.findById(123l));
    }
}