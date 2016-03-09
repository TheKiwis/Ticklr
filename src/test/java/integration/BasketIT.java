package integration;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by DucNguyenMinh on 08.03.16.
 */
public class BasketIT extends CommonIntegrationTest
{
    @Test
    public void shouldReturnBasket() throws Exception
    {
        mockMvc.perform(get("/users/123/basket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.basketItems").isArray());

    }

    @Test
    public void shouldAddItemToBasket() throws Exception
    {
        mockMvc.perform(post("/users/124/basket/items")
                .param("quantity", "10")
                .param("ticketSetId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.basketItems").isArray())
                .andExpect(jsonPath("$.basketItems").isNotEmpty());
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
