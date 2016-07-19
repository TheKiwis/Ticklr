package app.web.user;

import app.data.user.Buyer;
import app.web.ResourceURI;
import app.web.common.response.HrefResponse;
import app.web.common.response.expansion.Compact;
import app.web.common.response.expansion.Expandable;

import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 07.07.16
 */
@Expandable
public class BuyerResponse
{
    @Compact
    public UUID id;

    @Compact
    public String href;

    public String email;

    /**
     * @param buyer
     * @param resURI
     */
    public BuyerResponse(Buyer buyer, ResourceURI resURI)
    {
        id = buyer.getId();
        email = buyer.getIdentity().getEmail();
        href = resURI.getBuyerURI().buyerURL(buyer.getId());
    }
}
