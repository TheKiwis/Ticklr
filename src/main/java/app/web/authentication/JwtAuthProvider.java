package app.web.authentication;

import app.data.Identity;
import app.services.IdentityRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.UUID;

/**
 * @author ngnmhieu
 */
public class JwtAuthProvider implements AuthenticationProvider
{
    private IdentityRepository identityRepository;

    // performs JWT Token verification
    private JwtHelper authenticator;

    public JwtAuthProvider(IdentityRepository identityRepository, JwtHelper authenticator)
    {
        this.identityRepository = identityRepository;
        this.authenticator = authenticator;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        if (!supports(authentication.getClass()))
            return null;

        String credentials = (String) authentication.getCredentials();

        Claims authDetails = authenticator.authenticate(credentials);

        String subject = authDetails.getSubject();

        Identity id = null;
        try {
            id = identityRepository.findById(UUID.fromString(subject));
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid UUID", e);
        }

        if (id == null)
            throw new BadCredentialsException("Identity not found");

        JwtAuthToken authResult = new JwtAuthToken(id);

        authResult.setDetails(authDetails);

        return authResult;
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return (JwtAuthToken.class
                .isAssignableFrom(authentication));
    }
}
