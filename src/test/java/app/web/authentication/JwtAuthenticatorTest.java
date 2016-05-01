package app.web.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.token.Token;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by DucNguyenMinh on 29.02.16.
 */
public class JwtAuthenticatorTest
{

    private final String AUTHSECRET = "auth_secret";

    private JwtAuthenticator jwtAuthenticator;

    private String validToken;

    private String subject;

    @Before
    public void setUp(){
        this.subject = "user@example.com";

        Date expiredDate = getExpiredDate(JwtAuthenticator.DEFAULT_EXPIRED_DAYS);

        this.jwtAuthenticator = new JwtAuthenticator(AUTHSECRET);
        this.validToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(subject)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256,AUTHSECRET.getBytes(StandardCharsets.UTF_8)).compact();
    }


    @Test
    public void generate_shouldReturnJwtTokenExpiredIn10Days()
    {
        Date expiredDate = getExpiredDate(10);

        String jwtTokenExpiredIn10Days = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(subject)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256,AUTHSECRET.getBytes(StandardCharsets.UTF_8)).compact();

        Token jwtToken = jwtAuthenticator.generateToken(subject,10);
        assertEquals(jwtTokenExpiredIn10Days, jwtToken.getKey());
    }

    @Test
    public void generate_shouldReturnjwtTokenWithDefaultExpiration(){

        Token jwtToken = jwtAuthenticator.generateToken(subject);
        assertEquals(validToken, jwtToken.getKey());
    }


    @Test
    public void authenticate_shouldReturnValidClaims()
    {
        String email = "user@example.com";

        Claims claims = jwtAuthenticator.authenticate(validToken);

        assertEquals(email,claims.getSubject());
    }


    @Test(expected = BadCredentialsException.class)
    public void authenticate_shouldThrowBadCredentialsException()
    {
        JwtAuthenticator authenticator = new JwtAuthenticator("another_Secret");
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.Wx5VgMKG0VZaj7c-bi5ditbQOEQqb9YQyJHFLLvFMFs";
        authenticator.authenticate(token);
    }

    @Test(expected = BadCredentialsException.class)
    public void authenticate_shouldThrowBadCredentialsExceptionWhenMissingExpiration(){
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.Wx5VgMKG0VZaj7c-bi5ditbQOEQqb9YQyJHFLLvFMFs";

        jwtAuthenticator.authenticate(token);
    }

    @Test(expected = BadCredentialsException.class)
    public void authenticate_shouldThrowBadCredentialsExceptionWhenExpired(){

        String email = "user@example.com";

        String jwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(email)
                .setExpiration(new Date((new Date()).getTime() - 10000))
                .signWith(SignatureAlgorithm.HS256, AUTHSECRET.getBytes(StandardCharsets.UTF_8)).compact();

        jwtAuthenticator.authenticate(jwtToken);
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
