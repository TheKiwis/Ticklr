package app.web.basket.responses;

import app.data.*;
import app.web.ResourceURI;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ngnmhieu
 * @since 28.05.16
 */
public class BasketResponse
{
    public static class BuyerResponse
    {
        public String href;

        public BuyerResponse(Buyer buyer, ResourceURI resURI)
        {
            href = resURI.getBuyerURI().buyerURL(buyer.getId());
        }
    }

    public static class BasketItemsResponse
    {
        public String href;
        public List<BasketItemResponse> items;

        public BasketItemsResponse(Basket basket, ResourceURI resURI)
        {
            href = resURI.getBasketURI().basketItemURL(basket.getBuyer().getId(), null);
            items = basket.getItems().stream()
                    .map(item -> new BasketItemResponse(basket, item, resURI))
                    .collect(Collectors.toList());
        }
    }

    public static class TicketSetResponse
    {
        public long id;
        public String href;

        public TicketSetResponse(TicketSet ticketSet, ResourceURI resURI)
        {
            Event event = ticketSet.getEvent();
            href = resURI.getEventURI().ticketSetURL(event.getUser().getId(), event.getId(), ticketSet.getId());
            id = ticketSet.getId();
        }
    }

    public String href;

    public BuyerResponse buyer;

    public BasketItemsResponse items;

    public BasketResponse(Basket basket, ResourceURI resURI)
    {
        href = resURI.getBasketURI().basketURL(basket.getBuyer().getId());
        buyer = new BuyerResponse(basket.getBuyer(), resURI);
        items = new BasketItemsResponse(basket, resURI);
    }
}
