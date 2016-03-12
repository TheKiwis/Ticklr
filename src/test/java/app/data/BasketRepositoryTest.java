package app.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.method.P;

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

    BasketRepository basketRepository;

    @Before
    public void setUp() throws Exception
    {
        basketRepository = new BasketRepository(em);
    }

    @Test
    public void findByUserId_shouldReturnBasket() throws Exception
    {
        Basket mockBasket = mock(Basket.class);
        Query query = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(mockBasket);


        assertEquals(mockBasket, basketRepository.findByUserId(123l));
    }


    @Test
    public void findByUserId_shouldReturnNullIfNoResultFound()
    {
        Query query = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(NoResultException.class);


        assertNull(basketRepository.findByUserId(123l));
    }

    @Test
    public void save_shouldReturnTheSameBasket()
    {

        Basket mockBasket = mock(Basket.class);

        basketRepository.save(mockBasket);

        verify(em, atLeastOnce()).persist(mockBasket);
    }

    @Test
    public void saveOrUpdate_shouldReturnTheSameBasket()
    {

        Basket mockBasket = mock(Basket.class);

        basketRepository.saveOrUpdate(mockBasket);

        verify(em, atLeastOnce()).merge(mockBasket);

        mockBasket = mock(Basket.class);

        when(mockBasket.getId()).thenReturn(null);

        basketRepository.saveOrUpdate(mockBasket);

        verify(em, atLeastOnce()).persist(mockBasket);

    }

    @Test
    public void updateItem_shouldUpdateItemIfExist() throws Exception
    {
        BasketItem item = mock(BasketItem.class);
        when(item.getId()).thenReturn(123l);

        basketRepository.updateItem(item);

        verify(em, times(1)).merge(item);
    }

    @Test
    public void deleteItem_shouldRemoveItem()
    {
        BasketItem mockBasketItem = mock(BasketItem.class);

        when(em.merge(mockBasketItem)).thenReturn(mockBasketItem);
        when(mockBasketItem.getBasket()).thenReturn(mock(Basket.class));

        basketRepository.deleteItem(mockBasketItem);

        verify(em, times(1)).remove(mockBasketItem);
    }

}
