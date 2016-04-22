package integration;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.data.User;
import app.web.authentication.JwtAuthenticator;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Query;

/**
 * @author ngnmhieu
 */
public class UserIT extends CommonIntegrationTest
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

    private String userUri(Long id)
    {
        return "/api/users" + (id == null ? "" : "/" + id);
    }

    @Test
    public void shouldReturnUserInformation() throws Exception
    {
        mockMvc.perform(get(userUri(1l))
                .header("Authorization", loginString))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test

    public void shouldReturnHttpNotFoundIfNoUserFound() throws Exception
    {
        assertNull(em.find(User.class, 123l));

        mockMvc.perform(get(userUri(123l))
                .header("Authorization", loginString))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldSaveUser() throws Exception
    {
        Query query;

        query = em.createQuery("SELECT COUNT(u) FROM User u");
        long count = (long) query.getSingleResult();

        mockMvc.perform(post(userUri(null))
                .accept("application/json")
                .param("email", "unique_email@gmail.com")
                .param("password", "123456789"))
                .andExpect(header().string("Location", startsWith(userUri(null))));

        query = em.createQuery("SELECT COUNT(u) FROM User u");
        assertEquals((count + 1), query.getSingleResult());
    }

    @Test
    public void shouldReturnEmptyValidationErrorsAndCreatedStatus() throws Exception
    {
        mockMvc.perform(
                post(userUri(null))
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
                post(userUri(null))
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
                post(userUri(null))
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
