package app.web.authentication;

import app.data.User;
import app.web.user.UserController;
import app.web.user.UserURI;
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

    // constructs URLs for user resource
    private final UserURI userURI;

    private byte[] authSecretBytes;

    /**
     * @param authSecret
     * @param uri constructs URL for user resource
     */
    @Autowired
    public JwtHelper(@Value("${app.auth.secret}") String authSecret, UserURI uri)
    {
        this.authSecretBytes = authSecret.getBytes(StandardCharsets.UTF_8);
        this.userURI = uri;
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
     * @param user          subject claim
     * @param expiredInDays
     * @return JWT Token
     */
    public Token generateToken(User user, int expiredInDays)
    {
        Date expiredDate = getExpiredDate(expiredInDays);

        String jwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(user.getId().toString())
                .setExpiration(expiredDate)
                .claim("url", userURI.resourceURL(user.getId()))
                .signWith(SignatureAlgorithm.HS256, authSecretBytes).compact();
        DefaultToken token = new DefaultToken(jwtToken, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), "");
        return token;
    }

    /**
     * Generates JWT Token with the default expired day @see {@link JwtHelper ::DEFAULT_EXPIRED_DAYS}
     *
     * @param user user, for which of the token is generated (using user's ID or email)
     * @return
     */
    public Token generateToken(User user)
    {
        return this.generateToken(user, DEFAULT_EXPIRED_DAYS);
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
