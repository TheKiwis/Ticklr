package app.web.event.responses;

import app.data.event.Event;
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

    public TicketSetsResponse(Event event, ResourceURI resURI)
    {
        href = resURI.getEventURI().ticketSetURL(event.getUser().getId(), event.getId(), null);
        items = event.getTicketSets().stream()
                .map(ticketSet -> new TicketSetResponse(ticketSet, resURI))
                .collect(Collectors.toSet());
    }
}
