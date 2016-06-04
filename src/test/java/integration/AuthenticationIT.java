package integration;

import app.data.User;
import app.web.authentication.JwtHelper;
import app.web.buyer.BuyerURI;
import app.web.common.response.HrefResponse;
import app.web.user.UserURI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author ngnmhieu
 */
public class AuthenticationIT extends CommonIntegrationTest
{
    @Value("${app.auth.secret}")
    private String authSecret;

    @Value("${app.server.host}")
    private String hostname;

    private static final String AUTH_URI = "/api/auth/request-token";

    private UUID userId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");
    private UUID buyerId = UUID.fromString("49fa7f2a-2799-11e6-b67b-9e71128cae77");

    private User user;

    private JwtHelper jwt;

    private UserURI userURI;

    private BuyerURI buyerURI;

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        userURI = new UserURI(hostname);
        buyerURI = new BuyerURI(hostname);
        jwt = new JwtHelper(authSecret);
        user = (User) em.createQuery("SELECT u FROM User u WHERE u.identity.email = 'user@example.com'").getSingleResult();
    }

    // For the purpose of mapping json response to object - see getAuthTokenFor()
    private static class AuthToken
    {
        public String token;
        public HrefResponse user;
        public HrefResponse buyer;
    }

    public static String getAuthTokenFor(String email, String password, MockMvc mockMvc) throws Exception
    {
        class LoginForm
        {
            public String email;
            public String password;

            public LoginForm(String email, String password)
            {
                this.email = email;
                this.password = password;
            }
        }

        // perform the login
        MvcResult result = mockMvc.perform(post(AUTH_URI)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(new LoginForm(email, password)))
        ).andExpect(status().isOk()).andReturn();

        AuthToken token = new ObjectMapper().readValue(result.getResponse().getContentAsString(), AuthToken.class);

        return "Bearer " + token.token;
    }

    @Test
    public void happy_should_return_JWT_Token_with_valid_credential() throws Exception
    {
        String email = "user@example.com";

        String jwtToken = jwt.generateToken(user.getIdentity());

        mockMvc.perform(post(AUTH_URI)
                .contentType("application/json")
                .content(getAuthBody(email, "123456789")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(jwtToken))
                .andExpect(jsonPath("$.user.href").value(containsString(userURI.userURI(userId))))
                .andExpect(jsonPath("$.buyer.href").value(containsString(buyerURI.buyerURI(buyerId))));
    }

    @Test
    public void happy_should_Return_Resource_When_Provided_With_Valid_Jwt_Token() throws Exception
    {
        String jwtToken = jwt.generateToken(user.getIdentity());

        mockMvc.perform(get("/api/users/" + userId)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void sad_should_not_return_JWT_Token_with_invalid_credential() throws Exception
    {
        // invalid password
        mockMvc.perform(post(AUTH_URI)
                .contentType("application/json")
                .content(getAuthBody("user@example.com", "wrong_password")))
                .andExpect(status().isUnauthorized());

        // without request body
        mockMvc.perform(post(AUTH_URI)
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void sad_should_return_with_HTTP_Unauthorized_when_user_not_exist() throws Exception
    {
        String email = "nonexistentuser@example.com";
        long userCount = (long) em.createQuery("SELECT COUNT(u) FROM User u WHERE u.identity.email = :email").setParameter("email", email).getSingleResult();
        assertEquals(0, userCount);

        mockMvc.perform(post(AUTH_URI)
                .contentType("application/json")
                .content(getAuthBody(email, "123456789")))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void sad_should_not_return_resource_when_malformed_token() throws Exception
    {
        mockMvc.perform(get("/admin")
                .header("Authorization", "Bearer erggdfsyserysdghsrty.sdfghsdyyydfhys"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void sad_should_not_return_resource_token_signed_with_invalid_secret() throws Exception
    {
        String anotherSecret = "another_secret";

        String jwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(userId.toString())
                .setExpiration(new Date(System.currentTimeMillis() + (24 * 3600 * 1000)))
                .signWith(SignatureAlgorithm.HS256, anotherSecret.getBytes(StandardCharsets.UTF_8)).compact();


        mockMvc.perform(get("/admin")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void sad_should_not_return_resource_when_provided_with_expired_token() throws Exception
    {
        String jwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(userId.toString())
                .setExpiration(new Date(System.currentTimeMillis() - (24 * 3600 * 1000))) // expiration date in the past
                .signWith(SignatureAlgorithm.HS256, authSecret.getBytes(StandardCharsets.UTF_8)).compact();


        mockMvc.perform(get("/admin")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldLoadTestFixture() throws Exception
    {
        assertEquals("user@example.com", user.getIdentity().getEmail());
    }

    @Override
    protected IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("/fixtures/user_dataset.xml"));
    }

    private String getAuthBody(String email, String password) throws JsonProcessingException
    {
        class LoginForm
        {
            public String email;
            public String password;

            public LoginForm(String email, String password)
            {
                this.email = email;
                this.password = password;
            }
        }

        return new ObjectMapper().writeValueAsString(new LoginForm(email, password));
    }

}
