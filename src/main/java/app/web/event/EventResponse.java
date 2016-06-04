package app.web.event;

import app.data.Event;
import app.web.ResourceURI;
import app.web.basket.responses.BasketResponse;
import app.web.common.response.expansion.Compact;
import app.web.common.response.expansion.Expandable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ngnmhieu
 * @since 16.05.16
 */
@Expandable
public class EventResponse
{
    @Compact
    public Long id;

    @Compact
    public String href;

    public String title;

    public String description;

    public ZonedDateTime startTime;

    public ZonedDateTime endTime;

    public boolean canceled;

    public boolean isPublic;

    public boolean happening;

    public boolean expired;

    public TicketSetsResponse ticketSets;

    public EventResponse(Event event, ResourceURI resURI)
    {
        href = resURI.getEventURI().eventURL(event.getUser().getId(), event.getId());
        id = event.getId();
        title = event.getTitle();
        description = event.getDescription();
        startTime = event.getStartTime();
        endTime = event.getEndTime();
        canceled = event.isCanceled();
        isPublic = event.isPublic();
        happening = event.isHappening();
        expired = event.isExpired();
        ticketSets = new TicketSetsResponse(event, resURI);
        ticketSets.setEventResponse(this);
    }
}
