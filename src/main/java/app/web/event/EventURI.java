package app.web.event;

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
public class EventURI
{
    public static final String EVENTS_URI = "/api/users/{userId}/events";
    public static final String EVENT_URI = EVENTS_URI + "/{eventId}";
    public static final String TICKET_SETS_URI = EVENT_URI + "/ticket-sets";
    public static final String TICKET_SET_URI = TICKET_SETS_URI + "/{ticketSetId}";

    public static final String PUBLIC_EVENTS_URI = "/api/public/events";
    public static final String PUBLIC_EVENT_URI = PUBLIC_EVENTS_URI + "/{eventId}";

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
        return UriComponentsBuilder.fromUriString(EVENT_URI).buildAndExpand(userId, eventId).encode().toUriString();
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

        return UriComponentsBuilder.fromUriString(TICKET_SET_URI).buildAndExpand(userId, eventId, ticketSetId).encode().toUriString();
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
     * @param eventId
     * @return
     */
    public String publicEventURI(Long eventId)
    {
        return UriComponentsBuilder.fromUriString(PUBLIC_EVENT_URI).buildAndExpand(eventId).encode().toUriString();
    }

    /**
     * @param eventId
     * @return URL of the event resource exposed to the public
     */
    public String publicEventURL(Long eventId)
    {
        return hostname + publicEventURI(eventId);
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
