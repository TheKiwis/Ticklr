package app.web.event;

import app.data.Event;
import app.web.ResourceURI;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ngnmhieu
 * @since 02.06.16
 */
public class TicketSetsResponse
{
    public String href;

    public Set<TicketSetResponse> items;

    public TicketSetsResponse(Event event, ResourceURI resURI)
    {
        href = resURI.getEventURI().ticketSetURL(event.getUser().getId(), event.getId(), null);
        items = event.getTicketSets().stream()
                .map(ticketSet -> new TicketSetResponse(ticketSet, resURI))
                .collect(Collectors.toSet());
    }
}
