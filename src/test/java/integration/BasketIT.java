package integration;

import app.web.authentication.JwtAuthenticator;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by DucNguyenMinh on 08.03.16.
 */
public class BasketIT extends CommonIntegrationTest
{
    // authentication token
    private String authString;

    private String basketUri(Long userId)
    {
        return "/api/users/" + userId + "/basket";
    }

    private String basketItemUri(Long userId, Long basketItemId)
    {
        return basketUri(userId) + "/items" + (basketItemId == null ? "" : "/" + basketItemId);

    }

    @Before
    public void login() throws Exception
    {
        authString = AuthenticationIT.getAuthTokenFor("user_with_basket@example.com", "123456789", mockMvc);
    }

    @Test
    public void shouldReturnBasket() throws Exception
    {
        mockMvc.perform(get(basketUri(123l)).header("Authorization", authString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.basketItems").isArray());
    }

    @Test
    public void shouldAddItemToBasket() throws Exception
    {
        mockMvc.perform(post(basketItemUri(123l, null))
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
        mockMvc.perform(post(basketItemUri(123l, null))
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

        mockMvc.perform(post(basketItemUri(123l, null))
                .header("Authorization", authString)
                .param("quantity", "-2"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(basketItemUri(123l, null))
                .header("Authorization", authString)
                .param("quantity", "-2")
                .param("ticketSetId", "13"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(basketItemUri(123l, null))
                .header("Authorization", authString)
                .param("ticketSetId", "12"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldDeleteItem() throws Exception
    {
        assertEquals(1, (long) em.createQuery("SELECT COUNT(i) FROM BasketItem i WHERE i.id = :id").setParameter("id", 1l).getSingleResult());

        mockMvc.perform(delete(basketItemUri(123l, 1l))
                .header("Authorization", authString))
                .andExpect(status().isOk());

        assertEquals(0, (long) em.createQuery("select count(i) from BasketItem i where i.id = :id").setParameter("id", 1l).getSingleResult());
    }

    @Test
    public void shouldUpdateItem() throws Exception
    {
        assertEquals(1, (long) em.createQuery("SELECT COUNT(i) FROM BasketItem i WHERE i.id = :id").setParameter("id", 1l).getSingleResult());


        mockMvc.perform(put(basketItemUri(123l, 1l))
                .header("Authorization", authString)
                .param("quantity", "15"))
                .andExpect(status().isOk());

        assertEquals(1, (long) em.createQuery("select count(i) from BasketItem i where i.id = :id").setParameter("id", 1l).getSingleResult());
        assertEquals(15, (int) em.createQuery("select i.quantity from BasketItem i where i.id = :id").setParameter("id", 1l).getSingleResult());
    }

    @Test
    public void shouldValidateBasketItUpdateForm() throws Exception
    {

        mockMvc.perform(post(basketItemUri(123l, null))
                .header("Authorization", authString))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(basketItemUri(123l, null))
                .header("Authorization", authString)
                .param("ticketSetId", "13"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnForbiddenAccessWhileAccessingBasketOfOtherUser() throws Exception
    {
        Long anotherUserId = 124l;
        Long anotherBasketItemId = 456l;

        mockMvc.perform(get(basketUri(anotherUserId))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());

        mockMvc.perform(post(basketItemUri(anotherUserId, null))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());

        mockMvc.perform(put(basketItemUri(anotherUserId, anotherBasketItemId))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete(basketItemUri(anotherUserId, anotherBasketItemId))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());
    }

    /*******************
     * Fixture
     *******************/

    @Test
    public void shouldLoadTestFixture() throws Exception
    {
        assertNotNull(em.createQuery("SELECT u FROM User u WHERE u.id = 123").getSingleResult());
        assertNotNull(em.createQuery("SELECT b FROM Basket b WHERE b.id = 456").getSingleResult());
    }

    @Override
    protected IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("/fixtures/baskets_dataset.xml"));
    }
}
