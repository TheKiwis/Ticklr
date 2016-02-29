package app.web.authenticator;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.token.DefaultToken;
import org.springframework.security.core.token.Token;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Created by DucNguyenMinh on 29.02.16.
 */
public class JwtAuthenticator
{
    private String authSecret;

    public JwtAuthenticator(String authSecret)
    {
        this.authSecret = authSecret;
    }

    public Token generateToken(String email)
    {

        String jwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(email)
                .signWith(SignatureAlgorithm.HS256, authSecret.getBytes(StandardCharsets.UTF_8)).compact();
        DefaultToken token = new DefaultToken(jwtToken, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), "");
        return token;
    }
}
