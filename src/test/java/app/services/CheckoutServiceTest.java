package app.services;

import app.data.basket.Basket;
import app.services.checkout.CheckoutService;
import app.services.checkout.PaypalConfiguration;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author ngnmhieu
 * @since 11.06.16
 */
@RunWith(MockitoJUnitRunner.class)
public class CheckoutServiceTest
{
    CheckoutService service;

    @Mock
    EntityManager em;

    @Mock
    BasketService basketService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Payment mockPayment;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    APIContext mockAPIContext;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Basket mockBasket;

    // creates a new CheckoutService test object
    private CheckoutService createCheckOutService()
    {
        return new CheckoutServiceMocked(em, basketService, mockPayment, mockAPIContext);
    }

    @Test
    public void createPaypalPayment() throws Exception
    {
        String expectedApprovalUrl = "http://paypal.com/approval";
        Payment createdPayment = mock(Payment.class);
        when(mockPayment.create(mockAPIContext)).thenReturn(createdPayment);
        List<Links> links = new LinkedList();
        links.add(new Links(expectedApprovalUrl, "approval_url"));
        when(createdPayment.getLinks()).thenReturn(links);
        when(mockBasket.getItems()).thenReturn(new ArrayList());

        String approvalUrl = createCheckOutService().createPaypalPayment(mockBasket, "http://example.com/returnUrl", "http://example.com/cancelUrl");

        assertEquals(expectedApprovalUrl, approvalUrl);
        verify(mockPayment, times(1)).create(mockAPIContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createPaypalPayment_throwsExceptionIfBasketIsEmpty() throws Exception
    {
        createCheckOutService().createPaypalPayment(new Basket(), "return", "cancel");
    }

    // stub out factory methods
    static class CheckoutServiceMocked extends CheckoutService
    {
        private Payment mockPayment;

        private APIContext mockAPIContext;

        public CheckoutServiceMocked(EntityManager em, BasketService basketService, Payment mockPayment, APIContext mockAPIContext)
        {
            super(basketService, new PaypalConfiguration("clientID", "clientSecret"));
            this.setEntityManager(em);
            this.mockPayment = mockPayment;
            this.mockAPIContext = mockAPIContext;
        }

        @Override
        protected Payment createPayment()
        {
            return mockPayment;
        }

        @Override
        protected APIContext createAPIContext() throws PayPalRESTException
        {
            return mockAPIContext;
        }
    }
}