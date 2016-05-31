package app.web.buyer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 12.05.16
 */
@Component
public class BuyerURI
{
    public static final String BUYERS_URI = "/api/buyers";
    public static final String BUYER_URI = BUYERS_URI + "/{buyerId}";

    // hostname of the server on which the app is running
    private String hostname;

    protected BuyerURI() { }

    /**
     * @param hostname
     */
    @Autowired
    public BuyerURI(@Value("${app.server.host}") String hostname)
    {
        this.hostname = hostname;
    }

    /**
     * @param buyerId buyer's id; if buyerId == null, it's not appended
     * @return URI to the resource (absolute path)
     */
    public String buyerURI(UUID buyerId)
    {
        return BUYERS_URI + (buyerId == null ? "" : "/" + buyerId);
    }

    /**
     * @param buyerId
     * @return URL (including hostname) of the buyer resource
     */
    public String buyerURL(UUID buyerId)
    {
        return hostname + buyerURI(buyerId);
    }
}
