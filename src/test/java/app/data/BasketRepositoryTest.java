package app.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Created by DucNguyenMinh on 08.03.16.
 */
@RunWith(MockitoJUnitRunner.class)
public class BasketRepositoryTest
{
    @Mock
    EntityManager em;

    @Test
    public void findByUserId_shouldReturnBasket() throws Exception
    {
        Basket mockBasket = mock(Basket.class);
        Query query = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(mockBasket);

        BasketRepository basketRepository = new BasketRepository(em);

        assertEquals(mockBasket, basketRepository.findByUserId(123l));
    }


    @Test
    public void findByUserId_shouldReturnNullIfNoResultFound()
    {
        Query query = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(NoResultException.class);

        BasketRepository basketRepository = new BasketRepository(em);

        assertNull(basketRepository.findByUserId(123l));
    }

    @Test
    public void save_shouldReturnTheSameBasket()
    {

        BasketRepository basketRepository = new BasketRepository(em);
        Basket mockBasket = mock(Basket.class);

        basketRepository.save(mockBasket);

        verify(em, atLeastOnce()).persist(mockBasket);
    }

}
