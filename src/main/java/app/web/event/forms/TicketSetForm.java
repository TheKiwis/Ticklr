package app.web.event.forms;

import app.data.event.TicketSet;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author ngnmhieu
 * @since 02.06.16
 */
public class TicketSetForm
{
    @NotNull
    @Min(0)
    public BigDecimal price;

    @NotNull
    @NotEmpty
    public String title;

    @NotNull
    @Min(0)
    public Integer stock;

    public TicketSetForm(String title, BigDecimal price, Integer stock)
    {
        this.price = price;
        this.title = title;
        this.stock = stock;
    }

    protected TicketSetForm()
    {
    }

    public TicketSet getTicketSet()
    {
        return new TicketSet(title, price, stock);
    }

    public TicketSet getTicketSet(TicketSet ticketSet)
    {
        ticketSet.setTitle(title);
        ticketSet.setPrice(price);
        ticketSet.setStock(stock);
        return ticketSet;
    }
}
