package app.web.event.responses;

import app.data.event.Event;
import app.web.ResourceURI;
import app.web.common.response.expansion.Compact;
import app.web.common.response.expansion.Expandable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ngnmhieu
 * @since 19.07.16
 */
@Expandable
public class PublicEventsResponse
{
    @Compact
    public String href;

    public List<PublicEventResponse> items;

    public PublicEventsResponse(List<Event> events, ResourceURI resURI)
    {
        this.href = resURI.getEventURI().publicEventURL(null);
        this.items = events.stream().map(event -> new PublicEventResponse(event, resURI)).collect(Collectors.toList());
    }
}

