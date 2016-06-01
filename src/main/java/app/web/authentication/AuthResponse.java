package app.web.authentication;

import app.data.Buyer;
import app.data.User;
import app.web.ResourceURI;
import app.web.common.HrefResponse;

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
