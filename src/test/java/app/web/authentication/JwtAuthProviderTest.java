package app.web.authentication;

import app.data.Identity;
import app.data.User;
import app.services.IdentityRepository;
import app.services.UserRepository;
import io.jsonwebtoken.Claims;
import org.junit.Before;
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
public class JwtAuthProviderTest
{
    @Mock
    UserRepository userRepository;

    @Mock
    JwtHelper authenticator;

    @Mock
    private IdentityRepository identityRepository;

    private AuthenticationProvider authenticationProvider;

    @Before
    public void setUp() throws Exception
    {
        authenticationProvider = new JwtAuthProvider(identityRepository, authenticator);
    }

    @Test
    public void supports_shouldSupportOnlyJwtAuthenticationToken() throws Exception
    {
        assertTrue(authenticationProvider.supports(JwtAuthToken.class));
        assertFalse(authenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void shouldReturnPopulatedAuthenticationObject() throws Exception
    {
        UUID identityId = UUID.randomUUID();

        Identity mockIdentity = mock(Identity.class);
        when(identityRepository.findById(identityId)).thenReturn(mockIdentity);

        Authentication mockAuth = mock(JwtAuthToken.class);

        Claims mockClaim = mock(Claims.class);
        when(authenticator.authenticate(any())).thenReturn(mockClaim);
        when(mockClaim.getSubject()).thenReturn(identityId.toString());

        // create test object
        Authentication authResult = authenticationProvider.authenticate(mockAuth);

        assertTrue(authResult instanceof JwtAuthToken);
        assertEquals(mockIdentity, authResult.getPrincipal());
        assertEquals(mockClaim, authResult.getDetails());
        assertNull(authResult.getCredentials());
    }

    @Test(expected = AuthenticationException.class)
    public void shouldThrowAuthenticationExceptionOnBadToken() throws Exception
    {
        // mocks
        Authentication mockAuth = mock(JwtAuthToken.class);
        when(mockAuth.getCredentials()).thenReturn("credential");

        when(authenticator.authenticate(any())).thenThrow(BadCredentialsException.class);

        // create test object
        authenticationProvider.authenticate(mockAuth);
    }

    @Test(expected = BadCredentialsException.class)
    public void shouldThrowAuthenticationExceptionOnInvalidUUID() throws Exception
    {
        // mocks
        Authentication mockAuth = mock(JwtAuthToken.class);
        when(mockAuth.getCredentials()).thenReturn("credential");

        Claims claims = mock(Claims.class);
        when(authenticator.authenticate(any())).thenReturn(claims);
        when(claims.getSubject()).thenReturn("INVALID_UUID");

        // create test object
        authenticationProvider.authenticate(mockAuth);
    }

    @Test(expected = BadCredentialsException.class)
    public void shouldThrowAuthenticationExceptionIfIdentityNotFound() throws Exception
    {
        // mocks
        Authentication mockAuth = mock(JwtAuthToken.class);
        when(mockAuth.getCredentials()).thenReturn("credential");

        Claims claims = mock(Claims.class);
        when(authenticator.authenticate(any())).thenReturn(claims);
        when(claims.getSubject()).thenReturn(UUID.randomUUID().toString());
        when(identityRepository.findById(any())).thenReturn(null);

        // create test object
        authenticationProvider.authenticate(mockAuth);
    }

    @Test
    public void shouldReturnNullIfDoesNotSupportTheProvidedAuthenticationToken() throws Exception
    {
        // mocks
        Authentication mockAuth = mock(UsernamePasswordAuthenticationToken.class);
        when(mockAuth.getCredentials()).thenReturn("credential");

        // create test object
        assertFalse(authenticationProvider.supports(mockAuth.getClass()));
        assertNull(authenticationProvider.authenticate(mockAuth));

    }
}