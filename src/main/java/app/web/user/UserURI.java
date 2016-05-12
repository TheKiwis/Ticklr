package app.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 12.05.16
 */
@Component
public class UserURI
{
    public static final String USERS_URI = "/api/users";
    public static final String USER_URI = USERS_URI + "/{userId}";

    // hostname of the server on which the app is running
    private String hostname;

    protected UserURI() { }

    /**
     * @param hostname
     */
    @Autowired
    public UserURI(@Value("${app.server.host}") String hostname)
    {
        this.hostname = hostname;
    }

    /**
     * @param userId user's id; if userId == null, it's not appended
     * @return URI to the resource (absolute path)
     */
    public String userURI(UUID userId)
    {
        return USERS_URI + (userId == null ? "" : "/" + userId);
    }

    /**
     * @param userId
     * @return URL (including hostname) of the user resource
     */
    public String userURL(UUID userId)
    {
        return hostname + userURI(userId);
    }
}
