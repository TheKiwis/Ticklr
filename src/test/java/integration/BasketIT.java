package integration;

import app.web.authentication.JwtAuthenticator;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.exceptions.ExceptionIncludingMockitoWarnings;
import org.springframework.beans.factory.annotation.Value;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by DucNguyenMinh on 08.03.16.
 */
public class BasketIT extends CommonIntegrationTest
{
    @Value("${auth.secret}")
    private String authSecret;

    // authentication token
    private String loginString;

    @Before
    public void login() throws Exception
    {
        loginString = "Bearer " + new JwtAuthenticator(authSecret).generateToken("user@example.com").getKey();
    }

    @Test
    public void shouldReturnBasket() throws Exception
    {
        mockMvc.perform(get("/users/123/basket").header("Authorization", loginString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.basketItems").isArray());
    }

    @Test
    public void shouldAddItemToBasket() throws Exception
    {
        mockMvc.perform(post("/users/124/basket/items")
                .header("Authorization", loginString)
                .param("quantity", "10")
                .param("ticketSetId", "1"))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldIncrementBasketItemIfBasketItemWithTheSameTicketSetID() throws Exception
    {
        int quantity = (Integer) em.createQuery("SELECT i.quantity FROM BasketItem i WHERE i.id= :id").setParameter("id", 1l).getSingleResult();
        long count = (Long) em.createQuery("SELECT count(i) FROM BasketItem i").getSingleResult();
        mockMvc.perform(post("/users/123/basket/items")
                .header("Authorization", loginString)
                .param("quantity", "2")
                .param("ticketSetId", "2"))
                .andExpect(status().isCreated());

        assertEquals(quantity + 2, em.createQuery("SELECT i.quantity FROM BasketItem i WHERE i.id= :id").setParameter("id", 1l).getSingleResult());
        assertEquals(count, (long) em.createQuery("SELECT count(i) FROM BasketItem i").getSingleResult());
    }

    @Test
    public void shouldValidateBasketItemForm() throws Exception
    {

        mockMvc.perform(post("/users/123/basket/items")
                .header("Authorization", loginString)
                .param("quantity", "-2"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/users/123/basket/items")
                .header("Authorization", loginString)
                .param("quantity", "-2")
                .param("ticketSetId", "13"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/users/123/basket/items")
                .header("Authorization", loginString)
                .param("ticketSetId", "12"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldDeleteItem() throws Exception
    {
        assertEquals(1, (long) em.createQuery("SELECT COUNT(i) FROM BasketItem i WHERE i.id = :id").setParameter("id", 1l).getSingleResult());

        mockMvc.perform(delete("/users/123/basket/items/1")
                .header("Authorization", loginString))
                .andExpect(status().isOk());

        assertEquals(0, (long) em.createQuery("select count(i) from BasketItem i where i.id = :id").setParameter("id", 1l).getSingleResult());
    }

    @Test
    public void shouldUpdateItem() throws Exception
    {
        assertEquals(1,  (long) em.createQuery("SELECT COUNT(i) FROM BasketItem i WHERE i.id = :id").setParameter("id", 1l).getSingleResult());


        mockMvc.perform(put("/users/123/basket/items/1")
                .header("Authorization", loginString)
                .param("quantity", "15"))
                .andExpect(status().isOk());

        assertEquals(1, (long) em.createQuery("select count(i) from BasketItem i where i.id = :id").setParameter("id", 1l).getSingleResult());
        assertEquals(15 ,(int) em.createQuery("select i.quantity from BasketItem i where i.id = :id").setParameter("id", 1l).getSingleResult());
    }

    @Test
    public void shouldValidateBasketItUpdateForm() throws Exception
    {

        mockMvc.perform(post("/users/123/basket/items")
                .header("Authorization", loginString))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/users/123/basket/items")
                .header("Authorization", loginString)
                .param("ticketSetId", "13"))
                .andExpect(status().isBadRequest());
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
