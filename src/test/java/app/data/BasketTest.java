package app.data;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.when;

/**
 * @author DucNguyenMinh
 * @since 08.03.16.
 */
public class BasketTest
{
    @Test
    public void addItem() throws Exception
    {
        BasketItem item = mock(BasketItem.class);
        Basket basket = new Basket(mock(Buyer.class));

        assertTrue(basket.getItems().isEmpty());
        basket.addItem(item);
        assertTrue(basket.getItems().contains(item));
    }

    @Test
    public void isInBasket_TicketSet() throws Exception
    {
        TicketSet ticket = mock(TicketSet.class);
        TicketSet otherTicket = mock(TicketSet.class);

        Basket basket = new Basket();
        BasketItem item = new BasketItem(ticket, 10, BigDecimal.ONE);
        basket.addItem(item);

        assertTrue(basket.isInBasket(ticket));
        assertFalse(basket.isInBasket(otherTicket));
    }

    @Test
    public void isInBasket_BasketItem() throws Exception
    {
        TicketSet ticket = mock(TicketSet.class);
        TicketSet otherTicket = mock(TicketSet.class);

        Basket basket = new Basket();
        BasketItem item = new BasketItem(ticket, 10, BigDecimal.ONE);
        BasketItem otherItem = new BasketItem(otherTicket, 10, BigDecimal.ONE);
        basket.addItem(item);

        assertTrue(basket.isInBasket(item));
        assertFalse(basket.isInBasket(otherItem));
    }

    @Test
    public void getItemFor() throws Exception
    {
        TicketSet ticket = mock(TicketSet.class);
        TicketSet otherTicket = mock(TicketSet.class);

        Basket basket = new Basket();
        BasketItem item = new BasketItem(ticket, 10, BigDecimal.ONE);
        basket.addItem(item);

        assertEquals(item, basket.getItemFor(ticket));
        assertNull(basket.getItemFor(otherTicket));
    }
}
