package app.services;

import app.data.Basket;
import app.data.BasketItem;
import app.data.Buyer;
import app.data.TicketSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Created by DucNguyenMinh on 08.03.16.
 */
@RunWith(MockitoJUnitRunner.class)
public class BasketServiceTest
{
    @Mock
    EntityManager em;

    BasketService basketService;

    UUID userId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");

    @Before
    public void setUp() throws Exception
    {
        basketService = new BasketService(em);
    }

    @Test
    public void findByBuyerId_should_return_basket() throws Exception
    {
        Basket mockBasket = mock(Basket.class);
        Query query = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(mockBasket);


        assertEquals(mockBasket, basketService.findByBuyerId(userId));
    }


    @Test
    public void findByBuyerId_should_return_null_if_nothing_found()
    {
        Query query = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(NoResultException.class);


        assertNull(basketService.findByBuyerId(userId));
    }

    @Test
    public void save_should_return_the_same_basket()
    {

        Basket mockBasket = mock(Basket.class);

        basketService.save(mockBasket);

        verify(em, atLeastOnce()).persist(mockBasket);
    }

    @Test
    public void saveOrUpdate_shouldReturnTheSameBasket()
    {
        Basket mockBasket = mock(Basket.class);

        basketService.saveOrUpdate(mockBasket);

        verify(em, atLeastOnce()).merge(mockBasket);

        mockBasket = mock(Basket.class);

        when(mockBasket.getId()).thenReturn(null);

        basketService.saveOrUpdate(mockBasket);

        verify(em, atLeastOnce()).persist(mockBasket);
    }

    @Test
    public void updateItem_shouldUpdateItemIfExist() throws Exception
    {
        BasketItem item = mock(BasketItem.class);
        when(item.getId()).thenReturn(123l);

        basketService.updateItem(item);

        verify(em, times(1)).merge(item);
    }

    @Test
    public void removeItem()
    {
        Basket basket = new Basket(mock(Buyer.class));
        BasketItem item = new BasketItem(mock(TicketSet.class), 10, BigDecimal.ONE);
        basket.addItem(item);
        when(em.merge(item)).thenReturn(item);

        basketService.removeItem(basket, item);

        assertFalse(basket.isInBasket(item));
        verify(em, times(1)).remove(any(BasketItem.class));
    }

    @Test
    public void addItemToBasket_should_add_new_item_to_basket()
    {
        Basket basket = new Basket(mock(Buyer.class));

        TicketSet ticketSet = new TicketSet("To the moon", BigDecimal.TEN);

        BasketItem item = basketService.addItemToBasket(basket, ticketSet, 10);

        assertTrue(1 == basket.getItems().size());
        assertEquals(ticketSet, item.getTicketSet());
        assertEquals(ticketSet.getPrice(), item.getUnitPrice());
        assertTrue(10 == item.getQuantity());
        verify(em, times(1)).merge(basket);
    }

    @Test
    public void addItemToBasket_should_increment_quantity()
    {
        TicketSet ticketSet = new TicketSet("To the moon", BigDecimal.TEN);

        Basket basket = new Basket(mock(Buyer.class));

        BasketItem oldItem = basketService.addItemToBasket(basket, ticketSet, 10);
        BasketItem item = basketService.addItemToBasket(basket, ticketSet, 10);

        assertEquals(oldItem.getUnitPrice(), item.getUnitPrice());
        assertTrue(20 == item.getQuantity());
    }
}
