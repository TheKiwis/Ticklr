package app.web.basket.responses;

import app.data.basket.Basket;
import app.data.event.Event;
import app.data.event.TicketSet;
import app.data.user.Buyer;
import app.web.ResourceURI;

import java.math.BigDecimal;
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

    public String href;

    public BuyerResponse buyer;

    public BasketItemsResponse items;

    public BigDecimal totalPrice;

    public BasketResponse(Basket basket, ResourceURI resURI)
    {
        href = resURI.getBasketURI().basketURL(basket.getBuyer().getId());
        buyer = new BuyerResponse(basket.getBuyer(), resURI);
        totalPrice = basket.getTotalPrice();
        items = new BasketItemsResponse(basket, resURI);
    }
}
