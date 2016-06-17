package app.services.checkout;

import app.data.basket.Basket;
import app.data.basket.BasketItem;
import app.data.checkout.Order;
import app.data.checkout.OrderPosition;
import app.data.checkout.PaymentMethod;
import app.data.checkout.Ticket;
import app.data.event.TicketSet;
import app.services.basket.BasketService;
import app.web.checkout.forms.PurchaseForm;
import com.paypal.base.rest.PayPalRESTException;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ngnmhieu
 * @since 13.06.16
 */
@Service
public class CheckoutService
{
    // handles paypal payments and transactions
    private PaypalService paypalService;

    // manages the basket
    private BasketService basketService;

    private OrderRepository orderRepository;

    protected CheckoutService()
    {
    }

    /**
     * @param paypalService
     */
    @Autowired
    public CheckoutService(PaypalService paypalService, BasketService basketService, OrderRepository orderRepository)
   {
        this.paypalService = paypalService;
        this.basketService = basketService;
        this.orderRepository = orderRepository;
    }

    /**
     * @param basket
     * @param form   TODO: what if there is not enough ticket set?
     * @throws IllegalArgumentException if basket is empty
     * @throws PaypalService.NoPaymentException if no payment created before the execution
     * @throws PayPalRESTException if there is any error with Paypal REST API
     */
    public Order purchase(Basket basket, PurchaseForm form) throws PayPalRESTException
    {
        Assert.notNull(form);

        if (basket == null || basket.isEmpty())
            throw new IllegalArgumentException();

        paypalService.executePayment(basket, form.paypal.payerId);

        Order order = new Order(ZonedDateTime.now(), PaymentMethod.PAYPAL, basket.getBuyer());

        Map<Long, PurchaseForm.TicketInfo> ticketsMap = new HashMap<>();
        for (PurchaseForm.TicketInfo info : form.ticketInfos) {
            ticketsMap.put(info.ticketSetId, info);
        }

        List<BasketItem> basketItems = basket.getItems();
        for (BasketItem item : basketItems) {

            TicketSet ticketSet = item.getTicketSet();
            ticketSet.setStock(ticketSet.getStock() - item.getQuantity());

            // create new ticket for a ticket set
            PurchaseForm.TicketInfo info = ticketsMap.get(ticketSet.getId());
            Ticket ticket = new Ticket(info.firstName, info.lastName);

            OrderPosition position = new OrderPosition(ticketSet.getTitle(), item.getQuantity(), ticketSet.getPrice());
            position.setTicketSet(ticketSet);
            position.setTicket(ticket);

            order.addOrderPosition(position);
        }

        orderRepository.save(order);

        basketService.clearBasket(basket);

        return order;
    }
}
