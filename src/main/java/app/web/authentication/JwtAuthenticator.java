package app.web.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.token.DefaultToken;
import org.springframework.security.core.token.Token;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 *
 */
public class JwtAuthenticator
{
    private String authSecret;

    /**
     *
     * @param authSecret // todo authSecret used in jwt is byte[] array, consider taking byte array instead of String
     */
    public JwtAuthenticator(String authSecret)
    {
        this.authSecret = authSecret;
    }

    /**
     * Verifies the provided JWT Token
     *
     * @param token the JWT token
     * @return a set of claims contained in the JWT token
     * @throws org.springframework.security.authentication.BadCredentialsException if the followings happen:
     *             - malformed token
     *             - signature doesn't match
     *             - missing required claims (subject, expiration)
     *             - expired token
     */
    // todo check expiration, check require claims
    public Claims authenticate(String token)
    {
        return Jwts.parser().setSigningKey(authSecret.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody();
    }

    /**
     * Generates a JWT Token with provided information and signs it with authSecret
     *
     * @param email subject claim
     * @return JWT Token
     */
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
