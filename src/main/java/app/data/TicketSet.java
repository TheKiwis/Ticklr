package app.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author DucNguyenMinh
 * @since 07.03.16.
 */
@Entity
@Table(name = "ticket_sets")
public class TicketSet
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "price")
    @NotNull
    @Min(0)
    protected BigDecimal price;

    @Column(name = "title")
    @NotNull
    @NotEmpty
    protected String title;

    @Column(name = "stock")
    protected int stock;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    protected Event event;

    /**
     * @param title
     * @param price
     * @param stock
     */
    public TicketSet(String title, BigDecimal price, int stock)
    {
        this.price = price;
        this.title = title;
        this.stock = stock;
    }

    protected TicketSet()
    {
    }

    /**
     * @return ID of this ticket set
     */
    public Long getId()
    {
        return id;
    }

    public int getStock()
    {
        return stock;
    }

    public void setStock(int stock)
    {
        this.stock = stock;
    }

    /**
     * @return price of this ticket set
     */
    public BigDecimal getPrice()
    {
        return price;
    }

    /**
     * @param price of this ticket set
     */
    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    /**
     * @return title of this ticket set
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title title of thi ticket set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return the event that this ticket set belongs to
     */
    @JsonIgnore
    public Event getEvent()
    {
        return event;
    }

    /**
     * @param event the event that this ticket set belongs to
     */
    public void setEvent(Event event)
    {
        this.event = event;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TicketSet ticketSet = (TicketSet) o;

        return id != null ? id.equals(ticketSet.id) : ticketSet.id == null;

    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}
