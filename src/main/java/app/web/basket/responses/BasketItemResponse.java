package app.web.basket.responses;

import app.data.Basket;
import app.data.BasketItem;
import app.web.ResourceURI;

import java.math.BigDecimal;

/**
 * @author ngnmhieu
 * @since 01.06.16
 */
public class BasketItemResponse
{
    public String href;
    public long id;
    public int quantity;
    public BigDecimal unitPrice;
    public BigDecimal totalPrice;
    public BasketResponse.TicketSetResponse ticketSet;

    public BasketItemResponse(Basket basket, BasketItem item, ResourceURI resURI)
    {
        href = resURI.getBasketURI().basketItemURL(basket.getBuyer().getId(), item.getId());
        id = item.getId();
        quantity = item.getQuantity();
        unitPrice = item.getUnitPrice();
        totalPrice = item.getTotalPrice();
        ticketSet = new BasketResponse.TicketSetResponse(item.getTicketSet(), resURI);
    }
}
