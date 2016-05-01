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
     * @param authentication User's authentication information
     */
    public UserAuthorizer(Authentication authentication)
    {
        this.authentication = authentication;
    }

    /**
     * @param user the user, whose resources are being access
     *             (not to be confused with the user being authorized).
     * @return true if the authenticated user is authorized to resources of the user provided in argument
     */
    public boolean authorize(User user)
    {
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User))
            return false;

        return ((User) principal).getId().equals(user.getId());
    }
}
