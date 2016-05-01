package app.web.authentication;

import app.data.User;
import app.data.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author ngnmhieu
 */
public class JwtAuthenticationProvider implements AuthenticationProvider
{
    // UserRepository is used to fetch user
    private UserRepository userRepository;

    // performs JWT Token verification
    private JwtHelper authenticator;

    public JwtAuthenticationProvider(UserRepository userRepository, JwtHelper authenticator)
    {
        this.userRepository = userRepository;
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

        User user = null;
        try {
            user = userRepository.findById(UUID.fromString(subject));
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid UUID", e);
        }

        if (user == null)
            throw new BadCredentialsException("User not found");

        JwtAuthenticationToken authResult = new JwtAuthenticationToken(user, credentials, new ArrayList());

        authResult.setDetails(authDetails);

        return authResult;
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return (JwtAuthenticationToken.class
                .isAssignableFrom(authentication));
    }
}
