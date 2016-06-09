package app.services;

import app.data.user.Buyer;
import app.data.user.Identity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author ngnmhieu
 * @since 01.06.16
 */
@RunWith(MockitoJUnitRunner.class)
public class BuyerServiceTest
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    EntityManager em;

    private BuyerService buyerService;

    @Before
    public void setUp()
    {
        buyerService = new BuyerService(em);
    }

    @Test
    public void createWithIdentity() throws Exception
    {
        Identity mockIdentity = mock(Identity.class);
        Buyer buyer = buyerService.createWithIdentity(mockIdentity);

        assertEquals(mockIdentity, buyer.getIdentity());
        verify(em, times(1)).persist(any(Buyer.class));
    }

    @Test
    public void findByIdentity_found()
    {
        Identity mockIdentity = mock(Identity.class);
        Buyer buyer = new Buyer(mockIdentity);
        when(em.createQuery(anyString())
                .setParameter(anyString(), anyString())
                .getSingleResult()).thenReturn(buyer);

        assertEquals(buyer, buyerService.findByIdentity(mockIdentity));
    }

    @Test
    public void findByIdentity_not_found()
    {
        Identity mockIdentity = mock(Identity.class);

        when(em.createQuery(anyString())
                .setParameter(anyString(), anyString())
                .getSingleResult()).thenThrow(NoResultException.class);

        assertNull(buyerService.findByIdentity(mockIdentity));
    }
}