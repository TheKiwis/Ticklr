package app.web.authentication;

import app.data.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author ngnmhieu
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken
{
    // ~ Instance fields
    // ================================================================================================

    private final Object principal;
    private Object credentials;

    // ~ Constructors
    // ===================================================================================================

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>JwtAuthenticationToken</code>, as the {@link #isAuthenticated()}
     * will return <code>false</code>. The principal is not relevant until
     * the token is authenticated by <code>AuthenticationManager</code> or
     * <code>AuthenticationProvider</code>
     *
     * @param credentials the raw JWT Token
     */
    public JwtAuthenticationToken(String credentials)
    {
        super(null);
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
     * @param principal
     * @param credentials
     * @param authorities
     */
    public JwtAuthenticationToken(User principal, String credentials, Collection<? extends GrantedAuthority> authorities)
    {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
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
