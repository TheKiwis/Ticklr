package app.web.forms;

import app.data.Basket;
import app.data.BasketItem;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author DucNguyenMinh
 * @since 09.03.16
 */
public class BasketItemForm
{
    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    private Long ticketSetId;

    public BasketItemForm(Integer quantity, Long ticketSetId)
    {
        this.quantity = quantity;
        this.ticketSetId = ticketSetId;
    }

    protected BasketItemForm()
    {
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
