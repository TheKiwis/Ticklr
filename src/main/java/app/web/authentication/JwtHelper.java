package app.web.authentication;

import app.data.Identity;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.token.DefaultToken;
import org.springframework.security.core.token.Token;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

/**
 * {@link JwtHelper} performs JWT Token verification and generation.
 * Todo: avoid using Date Calendar, use java.time instead; consider changing args of generateToken to a class that represent time period better as int (e.g. Period)
 *
 * @author Duc Nguyen
 */
@Component
public class JwtHelper
{
    // default expiration days
    public static final int DEFAULT_EXPIRED_DAYS = 7;

    private byte[] authSecretBytes;

    /**
     * @param authSecret
     */
    @Autowired
    public JwtHelper(@Value("${app.auth.secret}") String authSecret)
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
     * @param id
     * @param expiredInDays
     * @return JWT Token
     */
    public String generateToken(Identity id, int expiredInDays)
    {
        Date expiredDate = getExpiredDate(expiredInDays);

        String jwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(id.getId().toString())
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, authSecretBytes).compact();

        return jwtToken;
    }

    /**
     * Generates JWT Token with the default expired day @see {@link JwtHelper ::DEFAULT_EXPIRED_DAYS}
     *
     * @param id the identity, for which of the token is generated
     * @return
     */
    public String generateToken(Identity id)
    {
        return this.generateToken(id, DEFAULT_EXPIRED_DAYS);
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
