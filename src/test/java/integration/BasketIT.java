package integration;

import app.web.basket.BasketURI;
import app.web.common.response.ErrorCodes;
import app.web.common.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author DucNguyenMinh
 * @since 08.03.16.
 */
public class BasketIT extends CommonIntegrationTest
{
    // authentication token
    private String authString;

    private UUID buyerOneId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");
    private UUID buyerTwoId = UUID.fromString("20fea260-0f14-11e6-89ca-0002a5d5c51b");

    private BasketURI basketURI;

    @Before
    public void setup() throws Exception
    {
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
    public void happy_should_return_basket() throws Exception
    {
        mockMvc.perform(prepareRequest(get(basketURI.basketURI(buyerOneId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.href").isNotEmpty())
                .andExpect(jsonPath("$.buyer").isMap())
                .andExpect(jsonPath("$.buyer.href").isNotEmpty())
                .andExpect(jsonPath("$.items").isMap())
                .andExpect(jsonPath("$.items.href").isNotEmpty())
                .andExpect(jsonPath("$.items.items").isArray());
    }

    @Test
    public void happy_should_return_a_basket_item() throws Exception
    {
        mockMvc.perform(prepareRequest(get(basketURI.basketItemURI(buyerOneId, 1l))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.href").isNotEmpty())
                .andExpect(jsonPath("$.quantity").value(8))
                .andExpect(jsonPath("$.unitPrice").isNumber())
                .andExpect(jsonPath("$.totalPrice").isNumber())
                .andExpect(jsonPath("$.ticketSet").isMap());
    }

    //@Test
    //public void happy_should_return_basket_items_collection() throws Exception
    //{
    //    mockMvc.perform(get(basketURI.basketItemURI(buyerOneId, null)).header("Authorization", authString))
    //            .andExpect(status().isOk())
    //            .andExpect(jsonPath("$.href").isNotEmpty())
    //            .andExpect(jsonPath("$.items").isArray());
    //}

    public static String getAddItemForm(Integer quantity, Long ticketSetId) throws JsonProcessingException
    {
        class AddItemForm
        {
            public Integer quantity;
            public Long ticketSetId;

            public AddItemForm(Integer quantity, Long ticketSetId)
            {
                this.quantity = quantity;
                this.ticketSetId = ticketSetId;
            }
        }

        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(new AddItemForm(quantity, ticketSetId));
    }

    @Test
    public void happy_should_add_item_to_basket() throws Exception
    {
        mockMvc.perform(prepareRequest(post(basketURI.basketItemURI(buyerOneId, null)))
                .content(getAddItemForm(10, 1l)))
                .andExpect(header().string("Location", startsWith(basketURI.basketItemURL(buyerOneId, null))))
                .andExpect(status().isCreated());
    }

    @Test
    public void happy_should_increment_quantity_if_ticketSet_already_added() throws Exception
    {
        int quantity = (Integer) em.createQuery("SELECT i.quantity FROM BasketItem i WHERE i.id= :id").setParameter("id", 1l).getSingleResult();
        long count = (Long) em.createQuery("SELECT count(i) FROM BasketItem i").getSingleResult();

        mockMvc.perform(prepareRequest(post(basketURI.basketItemURI(buyerOneId, null)))
                .content(getAddItemForm(2, 2l)))
                .andExpect(status().isCreated());

        assertEquals(quantity + 2, em.createQuery("SELECT i.quantity FROM BasketItem i WHERE i.id= :id").setParameter("id", 1l).getSingleResult());
        assertEquals(count, (long) em.createQuery("SELECT count(i) FROM BasketItem i").getSingleResult());
    }

    @Test
    public void happy_should_update_item() throws Exception
    {
        assertEquals(1, (long) em.createQuery("SELECT COUNT(i) FROM BasketItem i WHERE i.id = :id").setParameter("id", 1l).getSingleResult());

        mockMvc.perform(prepareRequest(put(basketURI.basketItemURI(buyerOneId, 1l)))
                .content("{\"quantity\": 15}"))
                .andExpect(status().isOk());

        assertEquals(1, (long) em.createQuery("select count(i) from BasketItem i where i.id = :id").setParameter("id", 1l).getSingleResult());
        assertEquals(15, (int) em.createQuery("select i.quantity from BasketItem i where i.id = :id").setParameter("id", 1l).getSingleResult());
    }


    @Test
    public void happy_should_delete_item() throws Exception
    {
        assertEquals(1, (long) em.createQuery("SELECT COUNT(i) FROM BasketItem i WHERE i.id = :id").setParameter("id", 1l).getSingleResult());

        mockMvc.perform(prepareRequest(delete(basketURI.basketItemURI(buyerOneId, 1l)))
                .header("Authorization", authString))
                .andExpect(status().isOk());

        assertEquals(0, (long) em.createQuery("select count(i) from BasketItem i where i.id = :id").setParameter("id", 1l).getSingleResult());
    }

    @Test
    public void sad_should_return_HTTP_NotFound() throws Exception
    {
        mockMvc.perform(prepareRequest(get(basketURI.basketURI(UUID.randomUUID()))))
                .andExpect(status().isNotFound());

        mockMvc.perform(prepareRequest(post(basketURI.basketItemURI(UUID.randomUUID(), null))))
                .andExpect(status().isNotFound());

        mockMvc.perform(prepareRequest(delete(basketURI.basketItemURI(UUID.randomUUID(), 999l))))
                .andExpect(status().isNotFound());

        mockMvc.perform(prepareRequest(delete(basketURI.basketItemURI(buyerOneId, 999l))))
                .andExpect(status().isNotFound());

        mockMvc.perform(prepareRequest(get(basketURI.basketItemURI(UUID.randomUUID(), 999l))))
                .andExpect(status().isNotFound());

        mockMvc.perform(prepareRequest(get(basketURI.basketItemURI(buyerOneId, 999l))))
                .andExpect(status().isNotFound());

        // TODO put...
    }

    @Test
    public void sad_should_not_delete_item_from_other_basket() throws Exception
    {
        assertEquals(1, (long) em.createQuery("SELECT COUNT(i) FROM BasketItem i WHERE i.id = :id").setParameter("id", 3l).getSingleResult());

        mockMvc.perform(prepareRequest(delete(basketURI.basketItemURI(buyerOneId, 3l))))
                .andExpect(status().isNotFound());

        assertEquals(1, (long) em.createQuery("SELECT COUNT(i) FROM BasketItem i WHERE i.id = :id").setParameter("id", 3l).getSingleResult());
    }

    @Test
    public void sad_should_validate_input_when_add_new_item() throws Exception
    {
        mockMvc.perform(prepareRequest(post(basketURI.basketItemURI(buyerOneId, null))))
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.VALIDATION_ERROR))
                .andExpect(status().isBadRequest());

        mockMvc.perform(prepareRequest(post(basketURI.basketItemURI(buyerOneId, null)))
                .content(getAddItemForm(3, null)))
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.VALIDATION_ERROR))
                .andExpect(status().isBadRequest());

        mockMvc.perform(prepareRequest(post(basketURI.basketItemURI(buyerOneId, null)))
                .content(getAddItemForm(-2, 13l)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(prepareRequest(post(basketURI.basketItemURI(buyerOneId, null)))
                .content(getAddItemForm(null, 12l)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void sad_should_validate_input_when_update_item() throws Exception
    {
        mockMvc.perform(prepareRequest(put(basketURI.basketItemURI(buyerOneId, 1l))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.VALIDATION_ERROR));

        mockMvc.perform(prepareRequest(put(basketURI.basketItemURI(buyerOneId, 1l)))
                .content("{\"quantity\": -1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.VALIDATION_ERROR));

        mockMvc.perform(prepareRequest(put(basketURI.basketItemURI(buyerOneId, 1l)))
                .content("{\"quantity\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.VALIDATION_ERROR));
    }

    @Test
    public void sad_should_return_HTTP_Forbidden() throws Exception
    {
        UUID anotherBuyerId = buyerTwoId;
        Long anotherBasketItemId = 1l;

        mockMvc.perform(prepareRequest(get(basketURI.basketURI(anotherBuyerId))))
                .andExpect(status().isForbidden());

        mockMvc.perform(prepareRequest(post(basketURI.basketItemURI(anotherBuyerId, null))))
                .andExpect(status().isForbidden());

        mockMvc.perform(prepareRequest(put(basketURI.basketItemURI(anotherBuyerId, anotherBasketItemId))))
                .andExpect(status().isForbidden());

        mockMvc.perform(prepareRequest(delete(basketURI.basketItemURI(anotherBuyerId, anotherBasketItemId))))
                .andExpect(status().isForbidden());

        mockMvc.perform(prepareRequest(get(basketURI.basketItemURI(anotherBuyerId, anotherBasketItemId))))
                .andExpect(status().isForbidden());
    }

    /*******************
     * Fixture
     *******************/

    @Test
    public void shouldLoadTestFixture() throws Exception
    {
        assertNotNull(em.createQuery("SELECT u FROM Buyer u WHERE u.id = :uuid").setParameter("uuid", buyerOneId).getSingleResult());
        assertNotNull(em.createQuery("SELECT u FROM Buyer u WHERE u.id = :uuid").setParameter("uuid", buyerOneId).getSingleResult());
        assertNotNull(em.createQuery("SELECT b FROM Basket b WHERE b.id = 456").getSingleResult());
    }

    @Override
    protected IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("/fixtures/baskets_dataset.xml"));
    }
}
