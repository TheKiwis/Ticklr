package integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.data.user.User;
import app.web.event.EventURI;
import app.web.user.UserURI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.persistence.Query;
import java.util.UUID;

/**
 * @author ngnmhieu
 */
public class UserIT extends CommonIntegrationTest
{
    // authentication token
    private String authString;

    UUID userId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");

    private UserURI userURI;

    private EventURI eventURI;

    @Before
    public void setup() throws Exception
    {
        userURI = new UserURI(hostname);
        eventURI = new EventURI(hostname);
        authString = AuthenticationIT.getAuthTokenFor("user@example.com", "123456789", mockMvc);
    }

    private MockHttpServletRequestBuilder prepareRequest(MockHttpServletRequestBuilder request)
    {
        return request
                .header("Authorization", authString)
                .contentType("application/json");
    }

    @Test
    public void happy_should_return_user_information() throws Exception
    {
        mockMvc.perform(prepareRequest(get(userURI.userURI(userId))))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.href").value(userURI.userURL(userId)))
                .andExpect(jsonPath("$.events.href").value(eventURI.eventURL(userId, null)));
    }

    @Test
    public void happy_should_create_user_and_buyer() throws Exception
    {
        Query queryUser, queryBuyer;

        long countUser = (long) em.createQuery("SELECT COUNT(u) FROM User u").getSingleResult();
        long countBuyer = (long) em.createQuery("SELECT COUNT(b) FROM Buyer b").getSingleResult();

        mockMvc.perform(post(userURI.userURI(null))
                .contentType("application/json")
                .content(registrationForm("unique_email@example.com", "123456789")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.auth.href").isNotEmpty());

        queryUser = em.createQuery("SELECT COUNT(u) FROM User u");
        queryBuyer = em.createQuery("SELECT COUNT(b) FROM Buyer b");
        assertEquals((countUser + 1), queryUser.getSingleResult());
        assertEquals((countBuyer + 1), queryBuyer.getSingleResult());
    }

    @Test
    public void happy_new_user_login_with_the_same_password() throws Exception
    {
        String email = "a_new_user@example.com";
        String password = "original_password";

        mockMvc.perform(post(userURI.userURI(null))
                .contentType("application/json")
                .content(registrationForm(email, password)))
                .andExpect(status().isCreated());

        String token = AuthenticationIT.getAuthTokenFor(email, password, mockMvc);
        assertTrue(token.startsWith("Bearer "));
    }

    @Test
    public void sad_should_return_HTTP_Forbidden_if_user_is_not_authorized() throws Exception
    {
        mockMvc.perform(prepareRequest(get(userURI.userURI(UUID.fromString("c4fcb3fe-1a98-11e6-b6ba-3e1d05defe78")))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void sad_should_return_HTTP_NotFound_if_no_user_found() throws Exception
    {
        UUID unknownUserId = UUID.fromString("f7fa0180-0f17-11e6-b94e-0002a5d5c51b");
        assertNull(em.find(User.class, unknownUserId));

        mockMvc.perform(prepareRequest(get(userURI.userURI(unknownUserId))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void sad_should_return_HTTP_BadRequest_with_validation_error() throws Exception
    {
        mockMvc.perform(prepareRequest(post(userURI.userURI(null)))
                .content(registrationForm("", "123456789")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void sad_should_return_HTTP_Conflict_if_email_existed() throws Exception
    {
        mockMvc.perform(prepareRequest(post(userURI.userURI(null)))
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
