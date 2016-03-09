package app.data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by DucNguyenMinh on 07.03.16.
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
    protected BigDecimal price;

    @Column(name = "title")
    protected String title;

    /**
     * @param price
     * @param title
     */
    public TicketSet(BigDecimal price, String title)
    {
        this.price = price;
        this.title = title;
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
}
