package app.web.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author ngnmhieu
 * @since 12.05.16
 */
@Component
public class AuthURI
{
    public static final String AUTH_URI = "/api/auth/request-token";

    // hostname of the server on which the app is running
    private String hostname;

    protected AuthURI()
    {
    }

    /**
     * @param hostname
     */
    @Autowired
    public AuthURI(@Value("${app.server.host}") String hostname)
    {
        this.hostname = hostname;
    }

    /**
     * @return URL to authentication endpoint (where client requests auth token)
     */
    public String authURL()
    {
        return hostname + AUTH_URI;
    }
}

