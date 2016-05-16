package app.web.event;

import app.data.Event;

import java.util.List;

/**
 * @author ngnmhieu
 * @since 16.05.16
 */
public class EventsResponse
{
    public String href;

    public List<EventResponse> items;

    public EventsResponse(String href, List<EventResponse> items)
    {
        this.href = href;
        this.items = items;
    }
}
