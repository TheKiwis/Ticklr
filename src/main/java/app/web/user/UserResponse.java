package app.web.user;

import app.data.user.User;
import app.web.ResourceURI;
import app.web.common.response.HrefResponse;
import app.web.common.response.expansion.Compact;
import app.web.common.response.expansion.Expandable;

import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 15.05.16
 */
@Expandable
public class UserResponse
{
    @Compact
    public UUID id;

    @Compact
    public String href;

    public String email;

    public HrefResponse events;

    /**
     * @param user
     * @param resURI
     */
    public UserResponse(User user, ResourceURI resURI)
    {
        id = user.getId();
        email = user.getIdentity().getEmail();
        href = resURI.getUserURI().userURL(user.getId());
        events = new HrefResponse(resURI.getEventURI().eventURL(user.getId(), null));
    }
}
