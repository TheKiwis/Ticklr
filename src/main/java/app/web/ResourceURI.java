package app.web;

import app.web.authentication.AuthURI;
import app.web.basket.BasketURI;
import app.web.buyer.BuyerURI;
import app.web.event.EventURI;
import app.web.user.UserURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ngnmhieu
 * @since 28.05.16
 */
@Component
public class ResourceURI
{
    private AuthURI authURI;

    private BasketURI basketURI;

    private UserURI userURI;

    private BuyerURI buyerURI;

    private EventURI eventURI;

    @Autowired
    public ResourceURI(AuthURI authURI, BasketURI basketURI, UserURI userURI, BuyerURI buyerURI, EventURI eventURI)
    {
        this.authURI = authURI;
        this.basketURI = basketURI;
        this.userURI = userURI;
        this.buyerURI = buyerURI;
        this.eventURI = eventURI;
    }

    public AuthURI getAuthURI()
    {
        return authURI;
    }

    public BasketURI getBasketURI()
    {
        return basketURI;
    }

    public UserURI getUserURI()
    {
        return userURI;
    }

    public BuyerURI getBuyerURI()
    {
        return buyerURI;
    }

    public EventURI getEventURI()
    {
        return eventURI;
    }
}
