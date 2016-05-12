package app.web.event;

import app.web.user.UserURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 12.05.16
 */
@Component
public class EventURI
{
    public static final String EVENTS_URI = "/api/users/{userId}/events";
    public static final String EVENT_URI = EVENTS_URI + "/{eventId}";
    public static final String TICKET_SETS_URI = EVENT_URI + "/ticket-sets";
    public static final String TICKET_SET_URI = TICKET_SETS_URI + "/{ticketSetId}";

    // hostname of the server on which the app is running
    private String hostname;

    protected EventURI() { }

    /**
     * @param hostname
     */
    @Autowired
    public EventURI(@Value("${app.server.host}") String hostname)
    {
        this.hostname = hostname;
    }

    /**
     * @param userId  not null
     * @param eventId is not included in the result if it's null
     * @return Event URL
     * @throws IllegalArgumentException if userId == null
     */
    public String eventURI(UUID userId, Long eventId)
    {
        if (userId == null)
            throw new IllegalArgumentException("userId must not be null.");
        return "/api/users/" + userId + "/events" + (eventId != null ? "/" + eventId : "");
    }

    /**
     * @param userId
     * @param eventId
     * @param ticketSetId is not included in the result if it's null
     * @return Ticket Set URL
     * @require userId != null
     * @require eventId != null
     */
    public String ticketSetURI(UUID userId, Long eventId, Long ticketSetId)
    {
        if (userId == null || eventId == null)
            throw new IllegalArgumentException("userId and eventId must not be null.");
        return eventURI(userId, eventId) + "/ticket-sets" + (ticketSetId == null ? "" : "/" + ticketSetId);
    }

    /**
     * @param userId != null
     * @param eventId if null then it will be ignored
     * @return URL (including hostname) of the event resource
     */
    public String eventURL(UUID userId, Long eventId)
    {
        return hostname + eventURI(userId, eventId);
    }

    /**
     * @param userId != null
     * @param eventId != null
     * @param ticketSetId if null then it will be ignored
     * @return URL (including hostname) of the event resource
     */
    public String ticketSetURL(UUID userId, Long eventId, Long ticketSetId)
    {
        return hostname + ticketSetURI(userId, eventId, ticketSetId);
    }
}
