package integration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.data.User;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Test;

import javax.persistence.Query;

/**
 * @author ngnmhieu
 */
public class UserManipulationIT extends CommonIntegrationTest
{
    @Test
    public void shouldSaveUser() throws Exception
    {
        Query query;

        query = em.createQuery("SELECT COUNT(u) FROM User u");
        long count = (long) query.getSingleResult();

        mockMvc.perform(
                post("/users/")
                        .accept("application/json")
                        .param("email", "unique_email@gmail.com")
                        .param("password", "123456789"));

        query = em.createQuery("SELECT COUNT(u) FROM User u");
        assertEquals((count+1), query.getSingleResult());
    }

    @Test
    public void shouldReturnEmptyValidationErrorsAndCreatedStatus() throws Exception
    {
        mockMvc.perform(
                post("/users/")
                        .accept("application/json")
                        .param("email", "email@gmail.com")
                        .param("password", "123456789"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void shouldReturnValidationErrorWithBadRequestHTTPStatus() throws Exception
    {
        mockMvc.perform(
                post("/users/")
                        .accept("application/json")
                        .param("email", "")
                        .param("password", "123456789"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void shouldRejectRequestWithDuplicatedEmail() throws Exception
    {
        mockMvc.perform(
                post("/users/")
                        .accept("application/json")
                        .param("email", "user@example.com")
                        .param("password", "123456789"))
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldLoadTestFixture() throws Exception
    {
        User u = (User) em.createQuery("SELECT u FROM User u WHERE u.email = 'user@example.com'").getSingleResult();
        assertEquals("user@example.com", u.getEmail());
    }

    @Override
    protected IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("/fixtures/user_dataset.xml"));
    }
}
