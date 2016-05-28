package app.data;

import org.junit.Test;
import static org.junit.Assert.*;

import static org.mockito.Mockito.mock;

/**
 * @author DucNguyenMinh
 * @since 08.03.16.
 */
public class BasketTest
{
    @Test
    public void addItem_shouldAddAnItemToBasket() throws Exception
    {
        BasketItem item = mock(BasketItem.class);
        Basket basket = new Basket(mock(Buyer.class));

        assertTrue(basket.getItems().isEmpty());
        basket.addItem(item);
        assertTrue(basket.getItems().contains(item));
    }
}
