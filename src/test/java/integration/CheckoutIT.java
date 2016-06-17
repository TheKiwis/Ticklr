package integration;

import app.data.basket.Basket;
import app.data.checkout.PaypalPayment;
import app.data.event.Event;
import app.web.basket.BasketURI;
import app.web.checkout.CheckoutURI;
import app.web.checkout.forms.PurchaseForm;
import app.web.common.response.ErrorCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.*;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.StringBody.exact;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author ngnmhieu
 * @since 11.06.16
 */
public class CheckoutIT extends CommonIntegrationTest
{
    private static ClientAndServer mockServer;

    // authentication token
    private String authString;

    private CheckoutURI checkoutURI;

    private UUID buyerWithBasketId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");

    private UUID buyerEmptyBasketId = UUID.fromString("d2d981e8-3017-11e6-ac61-9e71128cae77");

    private Basket basket;

    private BasketURI basketURI;

    @BeforeClass
    public static void setupMockPaypalServer() throws IOException
    {
        mockServer = startClientAndServer(1080);

        mockServer.when(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/v1/oauth2/token"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeaders(new Header("Content-Type", "application/json; charset=utf-8"))
                        .withBody(readResource("/fixtures/paypal/oauth_response.json")));

    }

    @AfterClass
    public static void tearDownMockServer()
    {
        mockServer.stop();
    }

    @Before
    public void setup() throws Exception
    {
        basket = (Basket) em.createQuery("SELECT b FROM Basket b WHERE b.buyer.id = :buyerId")
                .setParameter("buyerId", buyerWithBasketId).getSingleResult();
        checkoutURI = new CheckoutURI(hostname);
        basketURI = new BasketURI(hostname);
        authString = AuthenticationIT.getAuthTokenFor("buyer_with_basket@example.com", "123456789", mockMvc);
    }

    private MockHttpServletRequestBuilder prepareRequest(MockHttpServletRequestBuilder request)
    {
        return request
                .header("Authorization", authString)
                .contentType("application/json");
    }

    @Test
    public void happy_create_paypal_payment() throws Exception
    {
        setupHappyServerMock();

        ZonedDateTime time = ZonedDateTime.now().minusSeconds(2);
        mockMvc.perform(prepareRequest(post(checkoutURI.paypalPaymentURL(buyerWithBasketId)))
                .content(getPaypalPaymentForm()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.approvalUrl").isNotEmpty());

        PaypalPayment payment = (PaypalPayment) em.createQuery("SELECT p FROM PaypalPayment p WHERE p.id = :basketId")
                .setParameter("basketId", basket.getId()).getSingleResult();
        assertTrue(time.isBefore(payment.getCreatedTime()));
    }

    @Test
    public void happy_older_paypal_payment_should_be_removed() throws Exception
    {
        setupHappyServerMock();

        PaypalPayment paymentOne = (PaypalPayment) em.createQuery("SELECT p FROM PaypalPayment p WHERE p.id = :basketId")
                .setParameter("basketId", basket.getId()).getSingleResult();

        mockMvc.perform(prepareRequest(post(checkoutURI.paypalPaymentURL(buyerWithBasketId)))
                .content(getPaypalPaymentForm()))
                .andExpect(status().isCreated());


        PaypalPayment paymentTwo = (PaypalPayment) em.createQuery("SELECT p FROM PaypalPayment p WHERE p.id = :basketId")
                .setParameter("basketId", basket.getId()).getSingleResult();

        long count = (Long) em.createQuery("SELECT COUNT(p) FROM PaypalPayment p WHERE p.id = :basketId")
                .setParameter("basketId", basket.getId()).getSingleResult();

        assertEquals(1, count);
        assertNotEquals(paymentOne.getPaymentId(), paymentTwo.getPaymentId());
    }

    @Test
    public void happy_paypal_payment_should_be_deleted_if_basket_changes() throws Exception
    {
        setupHappyServerMock();

        // creates a new paypal payment
        mockMvc.perform(prepareRequest(post(checkoutURI.paypalPaymentURL(buyerWithBasketId)))
                .content(getPaypalPaymentForm()))
                .andExpect(status().isCreated());

        // adds an item to basket
        mockMvc.perform(prepareRequest(post(basketURI.basketItemURL(buyerWithBasketId, null)))
                .content(BasketIT.getAddItemForm(10, 2l)))
                .andExpect(status().isCreated());

        long count = (Long) em.createQuery("SELECT COUNT(p) FROM PaypalPayment p WHERE p.id = :basketId")
                .setParameter("basketId", basket.getId()).getSingleResult();
        // the payment should be deleted
        assertEquals(0, count);
    }

    @Test
    public void happy_execute_payment_and_create_order() throws Exception
    {
        setupHappyServerMock();

        List<PurchaseForm.TicketInfo> ticketInfos = Arrays.asList(new PurchaseForm.TicketInfo[]{
                new PurchaseForm.TicketInfo(1l, "Hieu", "Nguyen"),
                new PurchaseForm.TicketInfo(3l, "Duc", "Nguyen")
        });

        long orderCountOld = (Long) em.createQuery("SELECT COUNT(o) FROM Order o").getSingleResult();

        mockMvc.perform(prepareRequest(post(checkoutURI.purchaseExecuteURL(buyerWithBasketId)))
                .content(getPurchaseForm(ticketInfos)))
                .andExpect(status().isOk());

        long orderCountNew = (Long) em.createQuery("SELECT COUNT(o) FROM Order o").getSingleResult();

        assertEquals(orderCountOld + 1, orderCountNew);

        long basketItemCount = (Long) em.createQuery("SELECT COUNT(i) FROM BasketItem i WHERE i.basket.id = :basketId")
                .setParameter("basketId", 456l).getSingleResult();

        assertEquals(0, basketItemCount);
    }

    @Test
    public void happy_execute_payment_use_buyer_name_for_ticket_by_default() throws Exception
    {
    }

    @Test
    public void sad_payment_already_done() throws Exception
    {
    }

    @Test
    public void sad_no_payment_created_before_execution() throws Exception
    {
        setupHappyServerMock();

        String token = AuthenticationIT.getAuthTokenFor("buyer_with_empty_basket@example.com", "123456789", mockMvc);

        mockMvc.perform(post(checkoutURI.purchaseExecuteURL(buyerEmptyBasketId))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(getPurchaseForm(new ArrayList<>())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.PURCHASE_NO_PAYMENT));
    }

    private String getPurchaseForm(List<PurchaseForm.TicketInfo> ticketInfos) throws JsonProcessingException
    {
        ObjectMapper om = new ObjectMapper();
        return om.writerWithDefaultPrettyPrinter().writeValueAsString(
                new PurchaseForm(new PurchaseForm.PaypalPaymentInfo("JMKDKJ4D7DG7"), "PAYPAL", ticketInfos));
    }

    @Test
    public void sad_should_not_create_paypal_payment_if_basket_is_empty() throws Exception
    {
        String authToken = AuthenticationIT.getAuthTokenFor("buyer_with_empty_basket@example.com", "123456789", mockMvc);

        mockMvc.perform(post(checkoutURI.paypalPaymentURL(buyerEmptyBasketId))
                .header("Authorization", authToken)
                .contentType("application/json")
                .content(getPaypalPaymentForm()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void sad_should_return_Http_Errors() throws Exception
    {
        // access others' resource, HTTP Forbidden
        mockMvc.perform(prepareRequest(post(checkoutURI.paypalPaymentURL(buyerEmptyBasketId)))
                .content(getPaypalPaymentForm()))
                .andExpect(status().isForbidden());

        // resource that doesn't exist, HTTP Not Found
        mockMvc.perform(prepareRequest(post(checkoutURI.paypalPaymentURL(UUID.randomUUID()))))
                .andExpect(status().isNotFound());

        // validation error
        mockMvc.perform(prepareRequest(post(checkoutURI.paypalPaymentURL(buyerWithBasketId)))
                .content("{\"returnUrl\": \"example.com\"}"));
    }

    /*******************
     * Fixture
     *******************/

    @Test
    public void shouldLoadTestFixture() throws Exception
    {
        assertNotNull(em.find(Event.class, 2l));
    }

    @Override
    protected IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("/fixtures/checkout_dataset.xml"));
    }

    private String getPaypalPaymentForm() throws JsonProcessingException
    {
        class PaypalPaymentForm
        {
            public String returnUrl;
            public String cancelUrl;

            public PaypalPaymentForm(String returnUrl, String cancelUrl)
            {
                this.returnUrl = returnUrl;
                this.cancelUrl = cancelUrl;
            }
        }

        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(new PaypalPaymentForm("http://example.com/paypal/success", "http://example.com/paypal/cancel"));
    }

    // read text file and return its content
    private static String readResource(String resourcePath) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(CheckoutIT.class.getResourceAsStream(resourcePath)));
        String line = "";
        String result = "";
        while ((line = reader.readLine()) != null)
            result += line;

        return result;
    }

    private void setupHappyServerMock() throws IOException
    {
        mockServer.when(HttpRequest.request()
                .withMethod("POST")
                .withPath("/v1/payments/payment"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeaders(new Header("Content-Type", "application/json; charset=utf-8"))
                        .withBody(readResource("/fixtures/paypal/payment_response.json")));

        mockServer.when(HttpRequest.request()
                .withMethod("POST")
                .withPath("/v1/payments/payment/.*/execute"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeaders(new Header("Content-Type", "application/json; charset=utf-8"))
                        .withBody(readResource("/fixtures/paypal/execute_response.json")));
    }

}
