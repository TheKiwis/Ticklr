package app.integration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.config.RootConfig;
import app.config.WebConfig;
import app.data.User;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
