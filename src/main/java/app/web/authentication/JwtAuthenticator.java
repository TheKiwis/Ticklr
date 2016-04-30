package app.web.authentication;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.token.DefaultToken;
import org.springframework.security.core.token.Token;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

/**
 *
 */
public class JwtAuthenticator
{
    // default expiration days
    public static final int DEFAULT_EXPIRED_DAYS = 7;

    private byte[] authSecretBytes;

    /**
     * @param authSecret
     */
    public JwtAuthenticator(String authSecret)
    {
        this.authSecretBytes = authSecret.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Verifies the provided JWT Token
     *
     * @param token the JWT token
     * @return a set of claims contained in the JWT token
     * @throws org.springframework.security.authentication.BadCredentialsException if the followings happen:
     *                                                                             - malformed token
     *                                                                             - signature doesn't match
     *                                                                             - missing required claims (subject, expiration)
     *                                                                             - expired token
     */
    public Claims authenticate(String token)
    {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(authSecretBytes)
                    .parseClaimsJws(token)
                    .getBody();

            // check expiration claim
            if (claims.getExpiration() == null) {
                throw new BadCredentialsException("Missing expiration claim");
            }

            if (claims.getSubject() == null) {
                throw new BadCredentialsException("Missing subject claim");
            }

        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | ExpiredJwtException | MissingClaimException e) {
            throw new BadCredentialsException("JWT token is not valid");
        }

        return claims;
    }

    /**
     * Generates a JWT Token with provided information and signs it with authSecret
     *
     * @param sub           subject claim
     * @param expiredInDays
     * @return JWT Token
     */
    public Token generateToken(String sub, int expiredInDays)
    {
        Date expiredDate = getExpiredDate(expiredInDays);

        String jwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(sub)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, authSecretBytes).compact();
        DefaultToken token = new DefaultToken(jwtToken, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), "");
        return token;
    }

    /**
     * Generates JWT Token with the default expired day @see {@link JwtAuthenticator::DEFAULT_EXPIRED_DAYS}
     *
     * @param sub subject of the generated token (User's ID or Email)
     * @return
     */
    public Token generateToken(String sub)
    {
        return this.generateToken(sub, DEFAULT_EXPIRED_DAYS);
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
