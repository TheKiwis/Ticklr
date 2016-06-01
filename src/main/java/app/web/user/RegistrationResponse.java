package app.web.user;

import app.data.Buyer;
import app.data.User;
import app.web.ResourceURI;
import app.web.common.HrefResponse;

/**
 * @author ngnmhieu
 * @since 01.06.16
 */
public class RegistrationResponse
{
    public HrefResponse auth;

    public RegistrationResponse(ResourceURI resURI)
    {
        auth = new HrefResponse(resURI.getAuthURI().authURL());
    }
}
