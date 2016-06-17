package app.data.checkout;

import app.data.event.TicketSet;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author ngnmhieu
 * @since 09.06.16
 */
@Entity
@Table(name = "order_positions")
public class OrderPosition
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OneToOne
    @JoinColumn(name = "ticket_set_id", nullable = false)
    private TicketSet ticketSet;

    //@OneToOne(cascade = CascadeType.MERGE)
    //@JoinColumn(name = "ticket_id")
    @OneToOne(mappedBy = "orderPosition", cascade = CascadeType.MERGE)
    private Ticket ticket;

    protected OrderPosition() {}

    public OrderPosition(String title, Integer quantity, BigDecimal unitPrice)
    {
        this.title = title;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Integer getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public Order getOrder()
    {
        return order;
    }

    public void setOrder(Order order)
    {
        this.order = order;
    }

    public TicketSet getTicketSet()
    {
        return ticketSet;
    }

    public void setTicketSet(TicketSet ticketSet)
    {
        this.ticketSet = ticketSet;
    }

    public Ticket getTicket()
    {
        return ticket;
    }

    public void setTicket(Ticket ticket)
    {
        this.ticket = ticket;
        ticket.setOrderPosition(this);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderPosition that = (OrderPosition) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}
