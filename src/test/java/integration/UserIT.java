package integration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.data.User;
import app.web.basket.BasketURI;
import app.web.event.EventURI;
import app.web.user.UserURI;
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

    private BasketURI basketURI;

    private UserURI userURI;

    private EventURI eventURI;

    @Before
    public void setup()
    {
        userURI = new UserURI(hostname);
        eventURI = new EventURI(hostname);
        basketURI = new BasketURI(hostname);
    }

    @Before
    public void login() throws Exception
    {
        authHeader = AuthenticationIT.getAuthTokenFor("user@example.com", "123456789", mockMvc);
    }

    private String getUserURL(UUID id)
    {
        return userURI.userURI(id);
    }

    @Test
    public void happy_should_return_user_information() throws Exception
    {
        mockMvc.perform(get(getUserURL(userId))
                .header("Authorization", authHeader))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.href").value(userURI.userURL(userId)))
                .andExpect(jsonPath("$.events.href").value(eventURI.eventURL(userId, null)))
                .andExpect(jsonPath("$.basket.href").value(basketURI.basketURL(userId)));
    }

    @Test
    public void happy_should_create_user() throws Exception
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
    public void happy_new_user_login_with_the_same_password() throws Exception
    {
        String email = "a_new_user@example.com";
        String password = "original_password";

        mockMvc.perform(post(getUserURL(null))
                .accept("application/json")
                .contentType("application/json")
                .content(registrationForm(email, password)))
                .andExpect(status().isCreated());

        String token = AuthenticationIT.getAuthTokenFor(email, password, mockMvc);
        assertTrue(token.startsWith("Bearer "));
    }

    @Test
    public void happy_should_return_HTTP_Created_with_empty_validation_errors() throws Exception
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
    public void sad_should_return_HTTP_Forbidden_if_user_is_not_authorized() throws Exception
    {
        mockMvc.perform(get(getUserURL(UUID.fromString("c4fcb3fe-1a98-11e6-b6ba-3e1d05defe78")))
                .header("Authorization", authHeader))
                .andExpect(status().isForbidden());
    }

    @Test
    public void sad_should_return_HTTP_NotFound_if_no_user_found() throws Exception
    {
        UUID unknownUserId = UUID.fromString("f7fa0180-0f17-11e6-b94e-0002a5d5c51b");
        assertNull(em.find(User.class, unknownUserId));

        mockMvc.perform(get(getUserURL(unknownUserId))
                .header("Authorization", authHeader))
                .andExpect(status().isNotFound());
    }

    @Test
    public void sad_should_return_HTTP_BadRequest_with_validation_error() throws Exception
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
    public void sad_shouldRejectRequestWithDuplicatedEmail() throws Exception
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
        User u = (User) em.createQuery("SELECT u FROM User u WHERE u.identity.email = 'user@example.com'").getSingleResult();
        assertEquals("user@example.com", u.getIdentity().getEmail());
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
