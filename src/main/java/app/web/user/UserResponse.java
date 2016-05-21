package app.web.user;

import app.data.User;
import app.web.basket.BasketURI;
import app.web.event.EventURI;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 15.05.16
 */
public class UserResponse
{
    private static class BasketResponse
    {
        public BasketResponse(String href)
        {
            this.href = href;
        }

        public String href;
    }

    private static class EventsResponse
    {
        public String href;

        public EventsResponse(String href)
        {
            this.href = href;
        }
    }


    public UUID id;

    public String href;

    public String email;

    public EventsResponse events;

    public BasketResponse basket;

    /**
     * @param user
     * @param userURI
     * @param eventURI
     * @param basketURI
     */
    public UserResponse(User user, UserURI userURI, EventURI eventURI, BasketURI basketURI)
    {
        id = user.getId();
        email = user.getIdentity().getEmail();
        href = userURI.userURL(user.getId());
        events = new EventsResponse(eventURI.eventURL(user.getId(), null));
        basket = new BasketResponse(basketURI.basketURL(user.getId()));
    }
}
