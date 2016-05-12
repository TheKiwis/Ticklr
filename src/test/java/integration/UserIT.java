package integration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.data.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Query;
import java.util.UUID;

/**
 * @author ngnmhieu
 */
public class UserIT extends CommonIntegrationTest
{
    // authentication token
    private String authHeader;

    UUID userId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");

    @Before
    public void login() throws Exception
    {
        authHeader = AuthenticationIT.getAuthTokenFor("user@example.com", "123456789", mockMvc);
    }

    private String getUserURL(UUID id)
    {
        return "/api/users" + (id == null ? "" : "/" + id);
    }

    @Test
    public void shouldReturnUserInformation() throws Exception
    {
        mockMvc.perform(get(getUserURL(userId))
                .header("Authorization", authHeader))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test

    public void shouldReturnHttpNotFoundIfNoUserFound() throws Exception
    {
        UUID unknownUserId = UUID.fromString("f7fa0180-0f17-11e6-b94e-0002a5d5c51b");
        assertNull(em.find(User.class, unknownUserId));

        mockMvc.perform(get(getUserURL(unknownUserId))
                .header("Authorization", authHeader))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldSaveUser() throws Exception
    {
        Query query;

        query = em.createQuery("SELECT COUNT(u) FROM User u");
        long count = (long) query.getSingleResult();

        mockMvc.perform(post(getUserURL(null))
                .accept("application/json")
                .contentType("application/json")
                .content(registrationForm("unique_email@example.com", "123456789")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(hostname + getUserURL(null))));

        query = em.createQuery("SELECT COUNT(u) FROM User u");
        assertEquals((count + 1), query.getSingleResult());
    }

    @Test
    public void shouldReturnEmptyValidationErrorsAndCreatedStatus() throws Exception
    {
        mockMvc.perform(
                post(getUserURL(null))
                        .accept("application/json")
                        .contentType("application/json")
                        .content(registrationForm("email@gmail.com", "123456789")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void shouldReturnValidationErrorWithBadRequestHTTPStatus() throws Exception
    {
        mockMvc.perform(
                post(getUserURL(null))
                        .accept("application/json")
                        .contentType("application/json")
                        .content(registrationForm("", "123456789")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void shouldRejectRequestWithDuplicatedEmail() throws Exception
    {
        mockMvc.perform(
                post(getUserURL(null))
                        .accept("application/json")
                        .contentType("application/json")
                        .content(registrationForm("user@example.com", "123456789")))
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldLoadTestFixture() throws Exception
    {
        User u = (User) em.createQuery("SELECT u FROM User u WHERE u.email = 'user@example.com'").getSingleResult();
        assertEquals("user@example.com", u.getEmail());
    }

    private String registrationForm(String email, String password) throws JsonProcessingException
    {
        class RegistrationForm
        {
            public String email;
            public String password;

            public RegistrationForm(String email, String password)
            {
                this.email = email;
                this.password = password;
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(new RegistrationForm(email, password));
    }

    @Override
    protected IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("/fixtures/user_dataset.xml"));
    }
}
