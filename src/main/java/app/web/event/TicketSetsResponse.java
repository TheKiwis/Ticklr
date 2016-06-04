package app.web.event;

import app.data.Event;
import app.web.ResourceURI;
import app.web.common.response.expansion.Compact;
import app.web.common.response.expansion.Expandable;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ngnmhieu
 * @since 02.06.16
 */
@Expandable
public class TicketSetsResponse
{
    @Compact
    public String href;

    public Set<TicketSetResponse> items;

    // reference back to EventResponse, do not create
    // new instance in constructor, use setter instead
    private EventResponse eventResponse;

    public TicketSetsResponse(Event event, ResourceURI resURI)
    {
        href = resURI.getEventURI().ticketSetURL(event.getUser().getId(), event.getId(), null);
        items = event.getTicketSets().stream()
                .map(ticketSet -> new TicketSetResponse(ticketSet, resURI))
                .collect(Collectors.toSet());
    }

    public void setEventResponse(EventResponse eventResponse)
    {
        this.eventResponse = eventResponse;
        items.stream().forEach(item -> {
            item.setEventResponse(eventResponse);
        });
    }
}
