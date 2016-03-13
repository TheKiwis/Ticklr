package app.web.authorization;

import app.data.User;
import org.springframework.security.core.Authentication;

/**
 * Authorize authenticated user in the system
 *
 * @author ngnmhieu
 * @since 13.03.16
 */
public class UserAuthorizer
{
    private Authentication authentication;

    /**
     * @param authentication Authentication Information
     */
    public UserAuthorizer(Authentication authentication)
    {
        this.authentication = authentication;
    }

    /**
     * @param user User, whose resources are being access
     *             (not to be confused with the user being authorized).
     * @return true if the authenticated is authorized to the given user's resources
     */
    public boolean authorize(User user)
    {
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User))
            return false;

        return ((User) principal).getId().equals(user.getId());
    }
}
