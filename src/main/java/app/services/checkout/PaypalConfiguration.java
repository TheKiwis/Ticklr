package app.services.checkout;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ngnmhieu
 * @since 11.06.16
 */
public class PaypalConfiguration extends HashMap<String, String>
{
    // paypal clientID
    private String clientID;

    // paypal client secret
    private String clientSecret;

    /**
     * @param clientID
     * @param clientSecret
     */
    public PaypalConfiguration(String clientID, String clientSecret)
    {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
    }

    /**
     * @return Paypal client secret
     */
    public String getClientSecret()
    {
        return clientSecret;
    }

    /**
     * @return Paypal clientID
     */
    public String getClientID()
    {
        return clientID;
    }
}
