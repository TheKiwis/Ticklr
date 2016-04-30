package app.web.authentication;

import app.data.User;
import app.data.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author ngnmhieu
 */
public class JwtAuthententicationProvider implements AuthenticationProvider
{
    // UserRepository is used to fetch user
    private UserRepository userRepository;

    private JwtAuthenticator authenticator;

    public JwtAuthententicationProvider(UserRepository userRepository, JwtAuthenticator authenticator)
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

        // todo if IllegalArgument
        User user = userRepository.findById(UUID.fromString(subject));

        if (user == null) {
            // todo if user null then BadCredentialException
        }

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
