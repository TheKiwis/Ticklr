package app.web.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author ngnmhieu
 */
@RunWith(MockitoJUnitRunner.class)
public class JwtAuthenticationFilterTest
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    AuthenticationManager authenticationManager;

    @Mock
    HttpServletResponse servletResponse;

    @Mock
    HttpServletRequest servletRequest;

    @Mock
    FilterChain filterChain;

    @Mock
    AuthenticationEntryPoint entryPoint;

    @Test
    public void shouldNotCallFilterChainIfAuthenticationFails() throws Exception
    {
        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer header.body.signature");

        // mock authentication fails
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        Filter filter = new JwtAuthenticationFilter(authenticationManager, entryPoint);

        filter.doFilter(servletRequest, servletResponse, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    public void shouldCallAuthenticationEntryPointIfAuthenticationFails() throws Exception
    {
        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer header.body.signature");

        // mock authentication fails
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        Filter filter = new JwtAuthenticationFilter(authenticationManager, entryPoint);

        filter.doFilter(servletRequest, servletResponse, filterChain);

        verify(entryPoint, times(1)).commence(any(), any(), any());

    }

    @Test
    public void authenticationManagerShouldBePassedTheCorrectCredential() throws Exception
    {
        String principal = "user@example.com";
        String credential = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(principal)
                .signWith(SignatureAlgorithm.HS256, "test_secret".getBytes()).compact();

        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer " + credential);

        Filter filter = new JwtAuthenticationFilter(authenticationManager, entryPoint);

        filter.doFilter(servletRequest, servletResponse, filterChain);

        ArgumentCaptor<Authentication> argument = ArgumentCaptor.forClass(Authentication.class);

        verify(authenticationManager).authenticate(argument.capture());

        assertEquals(credential, argument.getValue().getCredentials());
    }

    @Test
    public void testAuthenticationSucceeds() throws Exception
    {
        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer header.body.signature");

        // mock authentication succeeds
        Filter filter = new JwtAuthenticationFilter(authenticationManager, entryPoint);

        filter.doFilter(servletRequest, servletResponse, filterChain);

        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    public void shouldContinueIfNoAuthorizationHeaderFound() throws Exception
    {
        when(servletRequest.getHeader("Authorization")).thenReturn(null);

        // mock authentication succeeds
        Filter filter = new JwtAuthenticationFilter(authenticationManager, entryPoint);

        filter.doFilter(servletRequest, servletResponse, filterChain);

        verify(filterChain, times(1)).doFilter(any(), any());
        verify(entryPoint, never()).commence(any(), any(), any());
    }

    @Test
    public void shouldSetAuthenticationObjectOnSecurityContextOnSuccess() throws Exception
    {
        // mock invalid authentication header
        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer header.body.signature");

        // cannot  mock static method SecurityContextHolder.getContext(), so we must set a mock SecurityContext
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);

        Authentication authResult = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authResult);

        Filter filter = new JwtAuthenticationFilter(authenticationManager, entryPoint);

        filter.doFilter(servletRequest, servletResponse, filterChain);

        verify(context, times(1)).setAuthentication(authResult);
    }
}
