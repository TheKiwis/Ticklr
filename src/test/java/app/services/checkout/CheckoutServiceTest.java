package app.services.checkout;

import app.data.basket.Basket;
import app.data.basket.BasketItem;
import app.data.checkout.Order;
import app.data.checkout.OrderPosition;
import app.data.checkout.PaymentMethod;
import app.data.event.TicketSet;
import app.data.user.Buyer;
import app.services.basket.BasketService;
import app.web.checkout.forms.PurchaseForm;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Error;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author ngnmhieu
 * @since 13.06.16
 */
@RunWith(MockitoJUnitRunner.class)
public class CheckoutServiceTest
{

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    PaypalService paypalService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    BasketService basketService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    OrderRepository orderRepository;

    CheckoutService checkoutService;

    @Before
    public void setUp() throws Exception
    {
        //checkoutService = new CheckoutService(paypalService, basketService, orderRepository);
        checkoutService = new CheckoutService(paypalService, basketService, orderRepository);
    }

    @Test
    public void purchase() throws Exception
    {
        TicketSet ts1 = new TicketSet("Ticket set 1", BigDecimal.TEN, 10);
        ts1.setId(1l);
        TicketSet ts2 = new TicketSet("Ticket set 2", BigDecimal.ONE, 10);
        ts2.setId(2l);
        Basket basket = new Basket(mock(Buyer.class));
        basket.addItem(new BasketItem(ts1, 5, BigDecimal.TEN));
        basket.addItem(new BasketItem(ts2, 2, BigDecimal.ONE));
        PurchaseForm form = new PurchaseForm()
                .addTicketInfo(1l, "Hieu", "Nguyen")
                .addTicketInfo(2l, "Duc", "Nguyen")
                .setPaymentMethod(PaymentMethod.PAYPAL.toString())
                .setPaypalPayer("ID-12AS34A3ND");

        when(paypalService.executePayment(basket, form.paypal.payerId))
                .thenReturn(mock(Payment.class));

        Order order = checkoutService.purchase(basket, form);

        verify(paypalService, times(1)).executePayment(basket, form.paypal.payerId);
        verify(basketService, times(1)).clearBasket(basket);
        assertEquals(5, ts1.getStock());
        assertEquals(8, ts2.getStock());
        assertEquals(2, order.getOrderPositions().size());
        OrderPosition position = order.getOrderPositions().get(0);
        assertNotNull(position.getTicket());
        assertEquals(ts1, position.getTicketSet());
    }

    @Test(expected = CheckoutService.PaymentAlreadyExecutedException.class)
    public void purchase_throw_PaymentAlreadyExecutedException() throws Exception
    {
        Basket basket = mock(Basket.class);
        when(basket.isEmpty()).thenReturn(false);
        PayPalRESTException exp = new PayPalRESTException("Payment already executed");
        exp.setDetails(new Error("PAYMENT_ALREADY_DONE", "", "", ""));
        when(paypalService.executePayment(any(), any())).thenThrow(exp);

        checkoutService.purchase(basket, new PurchaseForm()
                                .setPaymentMethod(PaymentMethod.PAYPAL.toString())
                                .setPaypalPayer("ID-12AS34A3ND"));
    }
}