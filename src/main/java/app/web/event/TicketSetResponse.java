package app.web.event;

import app.data.Event;
import app.data.TicketSet;
import app.data.User;
import app.web.ResourceURI;
import app.web.common.response.expansion.Compact;
import app.web.common.response.expansion.Expandable;

import java.math.BigDecimal;

/**
 * @author ngnmhieu
 * @since 02.06.16
 */
@Expandable
public class TicketSetResponse
{
    @Compact
    public Long id;

    @Compact
    public String href;

    public String title;

    public BigDecimal price;

    public Integer stock;

    // reference back to EventResponse, do not create
    // new instance in constructor, use setter instead
    public EventResponse event;

    public TicketSetResponse(TicketSet ticketSet, ResourceURI resURI)
    {
        id =  ticketSet.getId();
        title = ticketSet.getTitle();
        price = ticketSet.getPrice();
        stock = ticketSet.getStock();
        Event eventObj = ticketSet.getEvent();
        User user = eventObj.getUser();
        EventURI eventURI = resURI.getEventURI();
        href = eventURI.ticketSetURL(user.getId(), eventObj.getId(), ticketSet.getId());
    }

    public void setEventResponse(EventResponse event)
    {
        this.event = event;
    }
}
