package app.web.basket.responses;

import app.data.basket.Basket;
import app.data.basket.BasketItem;
import app.data.event.TicketSet;
import app.web.ResourceURI;
import app.web.event.responses.EventResponse;
import app.web.event.responses.TicketSetResponse;

import java.math.BigDecimal;

/**
 * @author ngnmhieu
 * @since 01.06.16
 */
public class BasketItemResponse
{
    public String href;

    public String title;

    public long id;

    public int quantity;

    public BigDecimal unitPrice;

    public BigDecimal totalPrice;

    public TicketSetResponse ticketSet;

    public BasketItemResponse(Basket basket, BasketItem item, ResourceURI resURI)
    {
        href = resURI.getBasketURI().basketItemURL(basket.getBuyer().getId(), item.getId());
        id = item.getId();
        title = item.getTicketSet().getTitle();
        quantity = item.getQuantity();
        unitPrice = item.getUnitPrice();
        totalPrice = item.getTotalPrice();
        TicketSet ts = item.getTicketSet();
        ticketSet = new TicketSetResponse(ts, resURI);
        ticketSet.setEventResponse(new EventResponse(ts.getEvent(), resURI));
    }
}
