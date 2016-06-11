package app.web.basket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 12.05.16
 */
@Component
public class BasketURI
{
    public static final String BASKET_URI = "/api/buyers/{buyerId}/basket";
    public static final String ITEMS_URI = BASKET_URI + "/items";
    public static final String ITEM_URI = ITEMS_URI + "/{itemId}";

    // hostname of the server on which the app is running
    private String hostname;

    protected BasketURI()
    {
    }

    /**
     * @param hostname
     */
    @Autowired
    public BasketURI(@Value("${app.server.host}") String hostname)
    {
        this.hostname = hostname;
    }

    /**
     * @param buyerId != null
     * @return URL to the basket resource
     */
    public String basketURL(UUID buyerId)
    {
        return hostname + basketURI(buyerId);
    }

    public String basketItemURL(UUID buyerId, Long basketItemId)
    {
        return hostname + basketItemURI(buyerId, basketItemId);
    }

    public String basketURI(UUID buyerId)
    {
        return UriComponentsBuilder.fromUriString(BASKET_URI).buildAndExpand(buyerId).encode().toUriString();
    }

    public String basketItemURI(UUID buyerId, Long itemId)
    {
        return UriComponentsBuilder.fromUriString(ITEM_URI).buildAndExpand(buyerId, itemId).encode().toUriString();
    }
}

