package app.services;

import app.data.basket.Basket;
import app.data.basket.BasketItem;
import app.data.user.Buyer;
import app.data.event.TicketSet;
import app.services.basket.BasketRepository;
import app.services.basket.BasketService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * @author DucNguyenMinh
 * @since 8.03.16
 */
@RunWith(MockitoJUnitRunner.class)
public class BasketServiceTest
{
    BasketService basketService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BasketRepository basketRepository;

    @Before
    public void setUp() throws Exception
    {
        basketService = new BasketService(basketRepository);
    }

    @Test
    public void removeItem()
    {
        Basket basket = new Basket(mock(Buyer.class));
        BasketItem item = new BasketItem(mock(TicketSet.class), 10, BigDecimal.ONE);
        basket.addItem(item);

        basketService.removeItem(basket, item);

        assertFalse(basket.isInBasket(item));
        verify(basketRepository, times(1)).save(basket);
    }

    @Test
    public void addItemToBasket_should_add_new_item_to_basket()
    {
        Basket basket = new Basket(mock(Buyer.class));
        when(basketRepository.save(basket)).thenReturn(basket);
        TicketSet ticketSet = new TicketSet("To the moon", BigDecimal.TEN, 50);
        BasketItem item = basketService.addItemToBasket(basket, ticketSet, 10);

        assertTrue(1 == basket.getItems().size());
        assertEquals(ticketSet, item.getTicketSet());
        assertEquals(ticketSet.getPrice(), item.getUnitPrice());
        assertTrue(10 == item.getQuantity());
        verify(basketRepository, times(1)).save(basket);
    }

    @Test(expected = BasketService.TicketOutOfStockException.class)
    public void addItemToBasket_should_throw_TicketOutOfStockException()
    {
        Basket basket = new Basket(mock(Buyer.class));
        TicketSet ticketSet = new TicketSet("To the moon", BigDecimal.TEN, 10);
        basketService.addItemToBasket(basket, ticketSet, 20);
    }

    @Test(expected = BasketService.TicketOutOfStockException.class)
    public void updateItemQuantity_should_throw_TicketOutOfStockException()
    {
        TicketSet ticketSet = new TicketSet("Sample", BigDecimal.TEN, 10);
        BasketItem item = new BasketItem(ticketSet, 9, BigDecimal.TEN);
        basketService.updateItemQuantity(item, 11);
    }

    @Test
    public void addItemToBasket_should_increment_quantity()
    {
        TicketSet ticketSet = new TicketSet("To the moon", BigDecimal.TEN, 50);
        Basket basket = new Basket(mock(Buyer.class));
        when(basketRepository.save(basket)).thenReturn(basket);

        BasketItem oldItem = basketService.addItemToBasket(basket, ticketSet, 10);
        BasketItem item = basketService.addItemToBasket(basket, ticketSet, 10);

        assertEquals(oldItem.getUnitPrice(), item.getUnitPrice());
        assertTrue(20 == item.getQuantity());
    }

    @Test
    public void updateItemQuantity() throws Exception
    {
        TicketSet ticketSet = new TicketSet("Sample", BigDecimal.TEN, 50);
        BasketItem item = new BasketItem(ticketSet, 10, BigDecimal.TEN);

        basketService.updateItemQuantity(item, 20);

        assertTrue(20 == item.getQuantity());
        verify(basketRepository, times(1)).save(any());
    }

    @Test
    public void clearBasket() throws Exception
    {
        Basket basket = new Basket();

        BasketItem item = new BasketItem(mock(TicketSet.class), 10, BigDecimal.TEN);

        basket.addItem(item);

        basketService.clearBasket(basket);

        assertTrue(basket.isEmpty());
        assertNull(item.getBasket());
    }
}
