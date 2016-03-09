package app.web.forms;

import app.data.Basket;
import app.data.BasketItem;

/**
 * Created by DucNguyenMinh on 09.03.16.
 */
public class BasketItemForm
{
    // todo validation

    private Integer quantity;

    private Long ticketSetId;

    public BasketItemForm(Integer quantity, Long ticketSetId)
    {
        this.quantity = quantity;
        this.ticketSetId = ticketSetId;
    }

    public Integer getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }

    public Long getTicketSetId()
    {
        return ticketSetId;
    }

    public void setTicketSetId(Long ticketSetId)
    {
        this.ticketSetId = ticketSetId;
    }
}
