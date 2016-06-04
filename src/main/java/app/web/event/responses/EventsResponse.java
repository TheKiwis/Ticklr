package app.web.event.responses;

import app.data.Event;
import app.data.User;
import app.web.ResourceURI;
import app.web.common.response.expansion.Compact;
import app.web.common.response.expansion.Expandable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ngnmhieu
 * @since 16.05.16
 */
@Expandable
public class EventsResponse
{
    @Compact
    public String href;

    public List<EventResponse> items;

    public EventsResponse(User user, List<Event> events, ResourceURI resURI)
    {
        this.href = resURI.getEventURI().eventURL(user.getId(), null);
        this.items = events.stream().map(event -> new EventResponse(event, resURI)).collect(Collectors.toList());
    }
}
