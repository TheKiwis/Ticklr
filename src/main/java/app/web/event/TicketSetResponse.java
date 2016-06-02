package app.web.event;

import app.data.Event;
import app.data.TicketSet;
import app.data.User;
import app.web.ResourceURI;
import app.web.common.HrefResponse;

import java.math.BigDecimal;

/**
 * @author ngnmhieu
 * @since 02.06.16
 */
public class TicketSetResponse
{
    public Long id;

    public String href;

    public String title;

    public BigDecimal price;

    public Integer stock;

    public HrefResponse event;

    public TicketSetResponse(TicketSet ticketSet, ResourceURI resourceURI)
    {
        id =  ticketSet.getId();
        title = ticketSet.getTitle();
        price = ticketSet.getPrice();
        stock = ticketSet.getStock();
        Event eventObj = ticketSet.getEvent();
        User user = eventObj.getUser();
        EventURI eventURI = resourceURI.getEventURI();
        href = eventURI.ticketSetURL(user.getId(), eventObj.getId(), ticketSet.getId());
        event = new HrefResponse(eventURI.eventURL(user.getId(), eventObj.getId()));
    }
}
