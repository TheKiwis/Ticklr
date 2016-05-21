package app.web.authorization;

import app.data.Identity;
import org.springframework.security.core.Authentication;

/**
 * Authorize authenticated identity in the system
 *
 * @author ngnmhieu
 * @since 13.03.16
 */
public class IdentityAuthorizer
{
    private Authentication authentication;

    /**
     * @require authentication != null
     * @param authentication User's authentication information
     */
    public IdentityAuthorizer(Authentication authentication)
    {
        if (authentication == null)
            throw new NullPointerException();

        this.authentication = authentication;
    }

    /**
     * @param identity the user's identity, whose resources are being access
     *             (not to be confused with the identity being authorized).
     * @return true if the authenticated identity is authorized to resources of the identity provided in argument
     */
    public boolean authorize(Identity identity)
    {
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Identity))
            return false;

        return ((Identity) principal).getId().equals(identity.getId());
    }
}
