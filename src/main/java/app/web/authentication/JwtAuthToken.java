package app.web.authentication;

import app.data.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * JwtAuthToken serves as a wrapper for a JWT Token, which will be used by JwtAuthProvider
 *
 * @author ngnmhieu
 */
public class JwtAuthToken extends AbstractAuthenticationToken
{
    // ~ Instance fields
    // ================================================================================================

    private final Object principal;
    private final Object credentials;

    // ~ Constructors
    // ===================================================================================================

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>JwtAuthToken</code>, as the {@link #isAuthenticated()}
     * will return <code>false</code>. The principal is not relevant until
     * the token is authenticated by <code>AuthenticationManager</code> or
     * <code>AuthenticationProvider</code>
     *
     * @param credentials the raw (maybe based64-encoded) JWT Token
     *
     * @ensure isAuthenticated() == false
     * @ensure getPrincipal() == null
     * @ensure getCredentials() == credentials
     * @ensure getAuthorities().isEmpty()
     */
    public JwtAuthToken(String credentials)
    {
        super(Collections.EMPTY_LIST);
        this.credentials = credentials;
        this.principal = null;
        setAuthenticated(false);
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or
     * <code>AuthenticationProvider</code> implementations that are satisfied with
     * producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication credentials.
     *
     * @param principal the identity of the authenticated user
     *
     * @ensure getPrincipal() == principal
     * @ensure getCredentials() == null
     * @ensure isAuthenticated() == true
     * @ensure getAuthorities().isEmpty()
     */
    public JwtAuthToken(Object principal)
    {
        super(Collections.EMPTY_LIST);
        this.principal = principal;
        this.credentials = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials()
    {
        return credentials;
    }

    @Override
    public Object getPrincipal()
    {
        return principal;
    }
}
