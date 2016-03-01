package app.web.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ngnmhieu
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    // authenticationManager authenticates the request
    private AuthenticationManager authenticationManager;

    // handle unauthorized request
    private AuthenticationEntryPoint entryPoint;

    /**
     * Constructs a new JwtAuthenticationFilter
     *
     * @param authenticationManager
     * @param entryPoint
     */
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint entryPoint)
    {
        this.authenticationManager = authenticationManager;
        this.entryPoint = entryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {

        AuthenticationException authenticationException = null;

        final boolean debug = logger.isDebugEnabled();

        boolean success = true;

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            success = false;
            authenticationException = new BadCredentialsException("No valid Authorization Header found.");
        }

        if (success) { // Authorization header found

            try {
                String token = header.substring(7);

                JwtAuthenticationToken authToken = new JwtAuthenticationToken(token);

                // authentication manager will throw AuthenticationException if the authentication fails
                Authentication authResult = authenticationManager.authenticate(authToken);

                if (debug) {
                    logger.debug("Authentication success: " + authResult);
                }

                SecurityContextHolder.getContext().setAuthentication(authResult);

                success = true;

            } catch (AuthenticationException failed) {

                success = false;

                authenticationException = failed;
            }

        }

        if (success) {

            filterChain.doFilter(request, response);

        } else {

            SecurityContextHolder.clearContext();

            // response via AuthenticationEntryPoint
            entryPoint.commence(request, response, authenticationException);

            if (debug) {
                logger.debug("Authentication request failed: " + authenticationException);
            }
        }
    }
}
