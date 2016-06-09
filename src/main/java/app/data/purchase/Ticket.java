package app.data.purchase;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author ngnmhieu
 * @since 09.06.16
 */
@Entity(name = "tickets")
public class Ticket
{
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "usage_time")
    private ZonedDateTime usageTime;

    @OneToOne
    @JoinColumn(name = "order_position_id", nullable = false)
    private OrderPosition orderPosition;

    protected Ticket()
    {
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getFullName()
    {
        return firstName + " " + lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public ZonedDateTime getUsageTime()
    {
        return usageTime;
    }

    public boolean isUsed()
    {
        return usageTime != null;
    }

    public void setUsageTime(ZonedDateTime usageTime)
    {
        this.usageTime = usageTime;
    }

    public OrderPosition getOrderPosition()
    {
        return orderPosition;
    }

    public void setOrderPosition(OrderPosition orderPosition)
    {
        this.orderPosition = orderPosition;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;

        return id != null ? id.equals(ticket.id) : ticket.id == null;

    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}