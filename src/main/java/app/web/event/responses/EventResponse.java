package app.web.event.responses;

import app.data.Event;
import app.web.ResourceURI;
import app.web.common.response.expansion.Compact;
import app.web.common.response.expansion.Expandable;

import java.time.ZonedDateTime;

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
    }

    public EventResponse(Event event, ResourceURI resURI, TicketSetsResponse ticketSets)
    {
        this(event, resURI);
        this.ticketSets = ticketSets;
    }


    public void setTicketSetsResponse(TicketSetsResponse ticketSets)
    {
        this.ticketSets = ticketSets;
    }
}
