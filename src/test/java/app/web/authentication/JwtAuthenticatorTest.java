package app.web.authentication;

import org.junit.Test;
import org.springframework.security.core.token.Token;

import static org.junit.Assert.assertEquals;

/**
 * Created by DucNguyenMinh on 29.02.16.
 */
public class JwtAuthenticatorTest
{

    private final String AUTHSECRET = "auth_secret";

    @Test
    public void shouldReturnJwtToken()
    {
        JwtAuthenticator jwtUtil = new JwtAuthenticator(AUTHSECRET);

        String email = "user@example.com";
        String password = "123456789";
        Token jwtToken = jwtUtil.generateToken(email);
        assertEquals("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.Wx5VgMKG0VZaj7c-bi5ditbQOEQqb9YQyJHFLLvFMFs",
                        jwtToken.getKey());
    }
}
