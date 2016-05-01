package app.web.authentication;

import app.data.User;
import app.data.UserRepository;
import io.jsonwebtoken.Claims;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author ngnmhieu
 */
@RunWith(MockitoJUnitRunner.class)
public class JwtAuthenticationProviderTest
{
    @Mock
    UserRepository userRepository;

    @Mock
    JwtHelper authenticator;
    @Test
    public void supports_shouldSupportOnlyJwtAuthenticationToken() throws Exception
    {
        assertTrue(new JwtAuthenticationProvider(userRepository, authenticator).supports(JwtAuthenticationToken.class));
        assertFalse(new JwtAuthenticationProvider(userRepository, authenticator).supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void shouldReturnPopulatedAuthenticationObject() throws Exception
    {
        UUID userId = UUID.randomUUID();
        String credential = "credential";

        // mocks
        User mockUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(mockUser);

        Authentication mockAuth = mock(JwtAuthenticationToken.class);
        when(mockAuth.getCredentials()).thenReturn(credential);

        Claims mockClaim = mock(Claims.class);
        when(authenticator.authenticate(any())).thenReturn(mockClaim);
        when(mockClaim.getSubject()).thenReturn(userId.toString());

        // create test object
        AuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(userRepository, authenticator);
        Authentication authResult = authenticationProvider.authenticate(mockAuth);

        assertTrue(authResult instanceof JwtAuthenticationToken);
        assertEquals(mockUser, authResult.getPrincipal());
        assertEquals(credential, authResult.getCredentials());
        assertEquals(mockClaim, authResult.getDetails());
    }

    @Test(expected = AuthenticationException.class)
    public void shouldThrowAuthenticationExceptionOnBadToken() throws Exception
    {
        // mocks
        Authentication mockAuth = mock(JwtAuthenticationToken.class);
        when(mockAuth.getCredentials()).thenReturn("credential");

        when(authenticator.authenticate(any())).thenThrow(BadCredentialsException.class);

        // create test object
        AuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(userRepository, authenticator);
        authenticationProvider.authenticate(mockAuth);
    }

    @Test(expected = BadCredentialsException.class)
    public void shouldThrowAuthenticationExceptionOnInvalidUUID() throws Exception
    {
        // mocks
        Authentication mockAuth = mock(JwtAuthenticationToken.class);
        when(mockAuth.getCredentials()).thenReturn("credential");

        Claims claims = mock(Claims.class);
        when(authenticator.authenticate(any())).thenReturn(claims);
        when(claims.getSubject()).thenReturn("INVALID_UUID");

        // create test object
        AuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(userRepository, authenticator);
        authenticationProvider.authenticate(mockAuth);
    }

    @Test(expected = BadCredentialsException.class)
    public void shouldThrowAuthenticationExceptionIfUserNotFound() throws Exception
    {
        // mocks
        Authentication mockAuth = mock(JwtAuthenticationToken.class);
        when(mockAuth.getCredentials()).thenReturn("credential");

        Claims claims = mock(Claims.class);
        when(authenticator.authenticate(any())).thenReturn(claims);
        when(claims.getSubject()).thenReturn(UUID.randomUUID().toString());
        when(userRepository.findById(any())).thenReturn(null);

        // create test object
        AuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(userRepository, authenticator);
        authenticationProvider.authenticate(mockAuth);
    }

    @Test
    public void shouldReturnNullIfDoesNotSupportTheProvidedAuthenticationToken() throws Exception
    {
        // mocks
        Authentication mockAuth = mock(UsernamePasswordAuthenticationToken.class);
        when(mockAuth.getCredentials()).thenReturn("credential");

        // create test object
        AuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(userRepository, authenticator);
        assertFalse(authenticationProvider.supports(mockAuth.getClass()));
        assertNull(authenticationProvider.authenticate(mockAuth));

    }

    // todo assign authorities for authorization
}