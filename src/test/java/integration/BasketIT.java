package integration;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private String basketUri(UUID buyerId)
    {
        return "/api/buyers/" + buyerId + "/basket";
    }

    private String basketItemUri(UUID buyerId, Long basketItemId)
    {
        return basketUri(buyerId) + "/items" + (basketItemId == null ? "" : "/" + basketItemId);
    }

    @Before
    public void login() throws Exception
    {
        authString = AuthenticationIT.getAuthTokenFor("buyer_with_basket@example.com", "123456789", mockMvc);
    }

    @Test
    public void shouldReturnBasket() throws Exception
    {
        mockMvc.perform(get(basketUri(buyerOneId)).header("Authorization", authString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.basketItems").isArray());
    }

    @Test
    public void shouldAddItemToBasket() throws Exception
    {
        mockMvc.perform(post(basketItemUri(buyerOneId, null))
                .header("Authorization", authString)
                .param("quantity", "10")
                .param("ticketSetId", "1"))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldIncrementBasketItemIfBasketItemWithTheSameTicketSetID() throws Exception
    {
        int quantity = (Integer) em.createQuery("SELECT i.quantity FROM BasketItem i WHERE i.id= :id").setParameter("id", 1l).getSingleResult();
        long count = (Long) em.createQuery("SELECT count(i) FROM BasketItem i").getSingleResult();
        mockMvc.perform(post(basketItemUri(buyerOneId, null))
                .header("Authorization", authString)
                .param("quantity", "2")
                .param("ticketSetId", "2"))
                .andExpect(status().isCreated());

        assertEquals(quantity + 2, em.createQuery("SELECT i.quantity FROM BasketItem i WHERE i.id= :id").setParameter("id", 1l).getSingleResult());
        assertEquals(count, (long) em.createQuery("SELECT count(i) FROM BasketItem i").getSingleResult());
    }

    @Test
    public void shouldValidateBasketItemForm() throws Exception
    {

        mockMvc.perform(post(basketItemUri(buyerOneId, null))
                .header("Authorization", authString)
                .param("quantity", "-2"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(basketItemUri(buyerOneId, null))
                .header("Authorization", authString)
                .param("quantity", "-2")
                .param("ticketSetId", "13"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(basketItemUri(buyerOneId, null))
                .header("Authorization", authString)
                .param("ticketSetId", "12"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldDeleteItem() throws Exception
    {
        assertEquals(1, (long) em.createQuery("SELECT COUNT(i) FROM BasketItem i WHERE i.id = :id").setParameter("id", 1l).getSingleResult());

        mockMvc.perform(delete(basketItemUri(buyerOneId, 1l))
                .header("Authorization", authString))
                .andExpect(status().isOk());

        assertEquals(0, (long) em.createQuery("select count(i) from BasketItem i where i.id = :id").setParameter("id", 1l).getSingleResult());
    }

    @Test
    public void shouldUpdateItem() throws Exception
    {
        assertEquals(1, (long) em.createQuery("SELECT COUNT(i) FROM BasketItem i WHERE i.id = :id").setParameter("id", 1l).getSingleResult());


        mockMvc.perform(put(basketItemUri(buyerOneId, 1l))
                .header("Authorization", authString)
                .param("quantity", "15"))
                .andExpect(status().isOk());

        assertEquals(1, (long) em.createQuery("select count(i) from BasketItem i where i.id = :id").setParameter("id", 1l).getSingleResult());
        assertEquals(15, (int) em.createQuery("select i.quantity from BasketItem i where i.id = :id").setParameter("id", 1l).getSingleResult());
    }

    @Test
    public void shouldValidateBasketItUpdateForm() throws Exception
    {

        mockMvc.perform(post(basketItemUri(buyerOneId, null))
                .header("Authorization", authString))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(basketItemUri(buyerOneId, null))
                .header("Authorization", authString)
                .param("ticketSetId", "13"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnForbiddenAccessWhileAccessingBasketOfOtherBuyer() throws Exception
    {
        UUID anotherBuyerId = buyerTwoId;
        Long anotherBasketItemId = 456l;

        mockMvc.perform(get(basketUri(anotherBuyerId))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());

        mockMvc.perform(post(basketItemUri(anotherBuyerId, null))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());

        mockMvc.perform(put(basketItemUri(anotherBuyerId, anotherBasketItemId))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete(basketItemUri(anotherBuyerId, anotherBasketItemId))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());
    }

    /*******************
     * Fixture
     *******************/

    @Test
    public void shouldLoadTestFixture() throws Exception
    {
        //assertNotNull(em.createQuery("SELECT u FROM Buyer u WHERE u.id = :uuid").setParameter("uuid", buyerOneId).getSingleResult());
        //assertNotNull(em.createQuery("SELECT u FROM Buyer u WHERE u.id = :uuid").setParameter("uuid", buyerOneId).getSingleResult());
        assertNotNull(em.createQuery("SELECT b FROM Basket b WHERE b.id = 456").getSingleResult());
    }

    @Override
    protected IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("/fixtures/baskets_dataset.xml"));
    }
}
