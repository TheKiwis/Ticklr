package app.web.basket;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author: DucNguyenMinh
 * @since: 09.03.16
 */
public class BasketItemUpdateForm
{
    @NotNull
    @Min(1)
    private Integer quantity;

    public BasketItemUpdateForm(Integer quantity)
    {
        this.quantity = quantity;
    }

    protected BasketItemUpdateForm()
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

}
