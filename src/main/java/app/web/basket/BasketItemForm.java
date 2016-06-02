package app.web.basket;

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
    public Integer quantity;

    @NotNull
    public Long ticketSetId;

    public BasketItemForm(Integer quantity, Long ticketSetId)
    {
        this.quantity = quantity;
        this.ticketSetId = ticketSetId;
    }

    protected BasketItemForm()
    {
    }
}
