package app.web.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 11.06.16
 */
@Component
public class CheckoutURI
{
    public static final String PAYPAL_INIT = "/api/buyers/{buyerId}/checkout/paypal/init";

    // hostname of the server on which the app is running
    private String hostname;

    protected CheckoutURI()
    {
    }

    /**
     * @param hostname
     */
    @Autowired
    public CheckoutURI(@Value("${app.server.host}") String hostname)
    {
        this.hostname = hostname;
    }

    /**
     * @param buyerId != null
     * @return URL to the basket resource
     */
    public String paypalInitURL(UUID buyerId)
    {
        return hostname + paypalInitURI(buyerId);
    }

    public String paypalInitURI(UUID buyerId)
    {
        return UriComponentsBuilder.fromUriString(PAYPAL_INIT).buildAndExpand(buyerId).encode().toUriString();
    }
}
