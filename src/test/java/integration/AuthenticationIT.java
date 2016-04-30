package integration;

import app.data.User;
import app.web.authentication.JwtAuthenticator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.token.Token;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author ngnmhieu
 */
public class AuthenticationIT extends CommonIntegrationTest
{
    @Value("${auth.secret}")
    private String authSecret;

    private static final String AUTH_URI = "/api/users/request-auth-token";
    private UUID userId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");

    @Test
    public void shouldLoadTestFixture() throws Exception
    {
        User u = (User) em.createQuery("SELECT u FROM User u WHERE u.email = 'user@example.com'").getSingleResult();
        assertEquals("user@example.com", u.getEmail());
    }

    // For the purpose of mapping json response to object - see login()
    private static class AuthToken {
        public String key;
        public Integer keyCreationTime;
        public String extendedInformation;
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

        return "Bearer " + token.key;
    }

    @Test
    public void shouldRespondWithJWTTokenWhenProvidedWithValidCredential() throws Exception
    {
        String email = "user@example.com";

        Date expiredDate = getExpiredDate(JwtAuthenticator.DEFAULT_EXPIRED_DAYS);

        String jwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(email)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, authSecret.getBytes(StandardCharsets.UTF_8)).compact();

        mockMvc.perform(post(AUTH_URI)
                .contentType("application/json")
                .content(getAuthBody(email, "123456789")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value(jwtToken));
    }

    @Test
    public void shouldRespondWithJWTTokenWhenProvidedWithInvalidCredential() throws Exception
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
    public void shouldRespondWithUnauthroziedWhenProvidedWithNonExistentUser() throws Exception
    {
        String email = "nonexistentuser@example.com";
        long userCount = (long) em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email").setParameter("email", email).getSingleResult();
        assertEquals(0, userCount);

        mockMvc.perform(post(AUTH_URI)
                .contentType("application/json")
                .content(getAuthBody(email, "123456789")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnResourceWhenProvidedWithValidJwtToken() throws Exception
    {
        JwtAuthenticator jwt = new JwtAuthenticator(authSecret);
        Token jwtToken = jwt.generateToken("user@example.com");

        mockMvc.perform(get("/api/users/" + userId)
                .header("Authorization", "Bearer " + jwtToken.getKey()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotReturnResouceWhenProviedWithMalformtoken() throws Exception
    {
        mockMvc.perform(get("/admin")
                .header("Authorization", "Bearer erggdfsyserysdghsrty.sdfghsdyyydfhys"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldNotReturnResouceWhenProvidedWithTokenSignedWithInvalidSecrete() throws Exception
    {

        String email = "user@example.com";
        String anotherSecret = "another_Secret";
        Date expiredDate = getExpiredDate(JwtAuthenticator.DEFAULT_EXPIRED_DAYS);

        String jwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(email)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, anotherSecret.getBytes(StandardCharsets.UTF_8)).compact();


        mockMvc.perform(get("/admin")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldNotReturnResouceWhenProvidedWithExpiredToken() throws Exception
    {
        String email = "user@example.com";

        Date expiredDate = getExpiredDate(-2);

        String jwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(email)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, authSecret.getBytes(StandardCharsets.UTF_8)).compact();


        mockMvc.perform(get("/admin")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isUnauthorized());
    }

    @Override
    protected IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("/fixtures/user_dataset.xml"));
    }

    private Date getExpiredDate(int expiredInDays)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, expiredInDays);
        return cal.getTime();
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
