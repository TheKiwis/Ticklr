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
    public static final String PAYPAL_PAYMENT = "/api/buyers/{buyerId}/checkout/paypal/payment";

    public static final String PURCHASE_EXECUTE = "/api/buyers/{buyerId}/checkout/purchase";

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
    public String paypalPaymentURL(UUID buyerId)
    {
        return hostname + paypalPaymentURI(buyerId);
    }

    public String paypalPaymentURI(UUID buyerId)
    {
        return UriComponentsBuilder.fromUriString(PAYPAL_PAYMENT).buildAndExpand(buyerId).encode().toUriString();
    }

    /**
     * @param buyerId != null
     * @return URL to the basket resource
     */
    public String purchaseExecuteURL(UUID buyerId)
    {
        return hostname + purchaseExecuteURI(buyerId);
    }

    public String purchaseExecuteURI(UUID buyerId)
    {
        return UriComponentsBuilder.fromUriString(PURCHASE_EXECUTE).buildAndExpand(buyerId).encode().toUriString();
    }
}
