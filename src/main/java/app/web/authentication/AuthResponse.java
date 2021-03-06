package app.web.authentication;

import app.data.user.Buyer;
import app.data.user.User;
import app.web.ResourceURI;
import app.web.common.response.HrefResponse;

/**
 * @author ngnmhieu
 * @since 01.06.16
 */
public class AuthResponse
{
    public String token;

    public HrefResponse user;

    public HrefResponse buyer;

    public AuthResponse(String token, User user, Buyer buyer, ResourceURI resURI)
    {
        this.token = token;
        this.user = new HrefResponse(resURI.getUserURI().userURL(user.getId()));
        this.buyer = new HrefResponse(resURI.getBuyerURI().buyerURL(buyer.getId()));
    }
}
