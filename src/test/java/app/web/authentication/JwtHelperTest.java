package app.web.authentication;

import app.data.Identity;
import app.data.User;
import app.web.user.UserURI;
import io.jsonwebtoken.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.token.Token;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author DucNguyenMinh
 * @since 29.02.16
 */
public class JwtHelperTest
{

    private final String AUTH_SECRET = "auth_secret";

    private final byte[] AUTH_SECRET_BYTE = AUTH_SECRET.getBytes(StandardCharsets.UTF_8);

    private JwtHelper jwtHelper;

    private String validToken;

    private String subject;

    private User user;

    private String hostname = "http://localhost";

    private String url = hostname + "api/users/sample-id";;

    @Before
    public void setUp()
    {
        user = new User(UUID.randomUUID(), new Identity("user@example.com", "123456789"));
        subject = user.getId().toString();

        Date expiredDate = getExpiredDate(JwtHelper.DEFAULT_EXPIRED_DAYS);

        this.jwtHelper = new JwtHelper(AUTH_SECRET, new UserURI(hostname));

        this.validToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(subject)
                .setExpiration(expiredDate)
                .claim("url", url)
                .signWith(SignatureAlgorithm.HS256, AUTH_SECRET_BYTE).compact();
    }


    @Test
    public void generate_shouldReturnJwtTokenExpiredIn10Days()
    {
        Date expiredDate = getExpiredDate(10);

        String jwtTokenExpiredIn10Days = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(subject)
                .setExpiration(expiredDate)
                .claim("url", url)
                .signWith(SignatureAlgorithm.HS256, AUTH_SECRET_BYTE).compact();


        Token jwtToken = jwtHelper.generateToken(user, 10);

        Jws<Claims> token = Jwts.parser().setSigningKey(AUTH_SECRET_BYTE).parseClaimsJws(jwtToken.getKey());
        Date exp = token.getBody().getExpiration();
        assertEquals(expiredDate, exp);
    }

    @Test
    public void generate_shouldReturnJwtTokenWithDefaultExpiration()
    {
        Date expectedExpiredDate = getExpiredDate(7);

        Token jwtToken = jwtHelper.generateToken(user);
        Jws<Claims> token = Jwts.parser().setSigningKey(AUTH_SECRET_BYTE).parseClaimsJws(jwtToken.getKey());
        Date exp = token.getBody().getExpiration();

        assertEquals(expectedExpiredDate, exp);
    }


    @Test
    public void authenticate_shouldReturnValidClaims()
    {
        Claims claims = jwtHelper.authenticate(validToken);

        assertEquals(subject, claims.getSubject());
        assertEquals(url, claims.get("url"));
    }


    @Test(expected = BadCredentialsException.class)
    public void authenticate_shouldThrowBadCredentialsException()
    {
        JwtHelper authenticator = new JwtHelper("another_Secret", new UserURI(hostname));
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.Wx5VgMKG0VZaj7c-bi5ditbQOEQqb9YQyJHFLLvFMFs";
        authenticator.authenticate(token);
    }

    @Test(expected = BadCredentialsException.class)
    public void authenticate_shouldThrowBadCredentialsExceptionWhenMissingExpiration()
    {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.Wx5VgMKG0VZaj7c-bi5ditbQOEQqb9YQyJHFLLvFMFs";

        jwtHelper.authenticate(token);
    }

    @Test(expected = BadCredentialsException.class)
    public void authenticate_shouldThrowBadCredentialsExceptionWhenExpired()
    {
        String email = "user@example.com";

        String jwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(email)
                .setExpiration(new Date((new Date()).getTime() - 10000))
                .signWith(SignatureAlgorithm.HS256, AUTH_SECRET.getBytes(StandardCharsets.UTF_8)).compact();

        jwtHelper.authenticate(jwtToken);
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
}
