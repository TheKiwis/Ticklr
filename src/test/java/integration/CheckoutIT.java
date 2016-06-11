package integration;

import app.data.event.Event;
import app.web.checkout.CheckoutURI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author ngnmhieu
 * @since 11.06.16
 */
public class CheckoutIT extends CommonTestTest
{
    // authentication token
    private String authString;

    private CheckoutURI checkoutURI;

    private UUID buyerWithBasketId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");
    private UUID buyerEmptyBasketId = UUID.fromString("d2d981e8-3017-11e6-ac61-9e71128cae77");

    @Before
    public void setup() throws Exception
    {
        checkoutURI = new CheckoutURI(hostname);
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
        mockMvc.perform(prepareRequest(post(checkoutURI.paypalInitURL(buyerWithBasketId)))
                .content(getPaypalInitForm()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.approvalUrl").isNotEmpty());
        //em.createQuery("")
    }

    @Test
    public void sad_should_not_create_paypal_payment_if_basket_is_empty() throws Exception
    {
        String authToken = AuthenticationIT.getAuthTokenFor("buyer_with_empty_basket@example.com", "123456789", mockMvc);

        mockMvc.perform(post(checkoutURI.paypalInitURL(buyerEmptyBasketId))
                .header("Authorization", authToken)
                .contentType("application/json")
                .content(getPaypalInitForm()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void sad_should_return_Http_Errors() throws Exception
    {
        // access others' resource, HTTP Forbidden
        mockMvc.perform(prepareRequest(post(checkoutURI.paypalInitURL(buyerEmptyBasketId)))
                .content(getPaypalInitForm()))
                .andExpect(status().isForbidden());

        // resource that doesn't exist, HTTP Not Found
        mockMvc.perform(prepareRequest(post(checkoutURI.paypalInitURL(UUID.randomUUID()))))
                .andExpect(status().isNotFound());

        // validation error
        mockMvc.perform(prepareRequest(post(checkoutURI.paypalInitURL(buyerWithBasketId)))
                .content("{\"returnUrl\": \"example.com\"}"));
    }

    private String getPaypalInitForm() throws JsonProcessingException
    {
        class PaypalInitForm
        {
            public String returnUrl;
            public String cancelUrl;

            public PaypalInitForm(String returnUrl, String cancelUrl)
            {
                this.returnUrl = returnUrl;
                this.cancelUrl = cancelUrl;
            }
        }

        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(new PaypalInitForm("http://example.com/paypal/success", "http://example.com/paypal/cancel"));
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
}
