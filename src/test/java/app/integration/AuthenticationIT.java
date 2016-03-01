package app.integration;

import app.config.RootConfig;
import app.config.WebConfig;
import app.data.User;
import app.web.authentication.JwtAuthenticator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.token.Token;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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

    @Test
    public void shouldLoadTestFixture() throws Exception
    {
        User u = (User) em.createQuery("SELECT u FROM User u WHERE u.email = 'user@example.com'").getSingleResult();
        assertEquals("user@example.com", u.getEmail());
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
                .signWith(SignatureAlgorithm.HS256,authSecret.getBytes(StandardCharsets.UTF_8)).compact();

        mockMvc.perform(
                post("/users/request-auth-token")
                        .param("email", email)
                        .param("password", "123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value(jwtToken));
    }

    @Test
    public void shouldRespondWithJWTTokenWhenProvidedWithInvalidCredential() throws Exception
    {
        mockMvc.perform(
                post("/users/request-auth-token")
                        .param("email", "user@example.com")
                        .param("password", "wrong_pasword"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldRespondWithUnauthroziedWhenProvidedWithNonExistentUser() throws Exception
    {
        String email = "nonexistentuser@example.com";
        long userCount = (long) em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email").setParameter("email", email).getSingleResult();
        assertEquals(0, userCount);

        mockMvc.perform(
                post("/users/request-auth-token")
                        .param("email", email)
                        .param("password", "123456789"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnResourceWhenProvidedWithValidJwtToken() throws Exception
    {
        JwtAuthenticator jwt = new JwtAuthenticator(authSecret);
        Token jwtToken = jwt.generateToken("user@example.com");

        mockMvc.perform(get("/admin")
                .header("Authorization", "Bearer " + jwtToken.getKey()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotReturnResourceWhenProvidedWithoutJwtAuthenticationToken() throws Exception
    {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
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
        cal.add(Calendar.DATE,expiredInDays);
        return cal.getTime();
    }
}
