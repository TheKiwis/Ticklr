package app.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * @author ngnmhieu
 */
@Entity
@Table(name = "events")
public class Event
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "title")
    @NotNull @NotEmpty
    protected String title;

    @Column(name = "description")
    protected String description;

    @Column(name = "start_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    protected LocalDateTime startTime;

    @Column(name = "end_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    protected LocalDateTime endTime;

    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    protected Visibility visibility;

    @Column(name = "canceled")
    protected boolean canceled;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    protected User user;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    protected Collection<TicketSet> ticketSets = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time", updatable = false)
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    protected Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_time", insertable = false, updatable = false)
    @Generated(GenerationTime.ALWAYS)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    protected Date updatedTime;

    /**
     * Default Constructor assigns default values to fields
     *  - title = New Event
     *  - description is empty
     *  - startTime = now + 7 days
     *  - endTime = startTime + 1 hours
     *  - status = DRAFT
     *  - visibility = PRIVATE
     *  - canceled = false
     */
    public Event()
    {
        this("New Event", "", null, null, Visibility.PRIVATE, false, null);

        setStartTime(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(7));
        setEndTime(startTime.plusHours(1));
    }

    /**
     * @param title event's title
     * @param description event's description
     * @param startTime time when the event starts
     * @param endTime time when the event ends
     * @param visibility visibility of the event @see Visibility
     * @param canceled has this event been canceled
     * @param user the user who owns this event
     */
    public Event(String title, String description, LocalDateTime startTime, LocalDateTime endTime, Visibility visibility, boolean canceled, User user)
    {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.visibility = visibility;
        this.canceled = canceled;
        this.user = user;
    }

    /**
     * @return visibility of this event
     * @see Visibility for possible values
     */
    public Visibility getVisibility()
    {
        return visibility;
    }

    /**
     * @param visibility of this event
     */
    public void setVisibility(Visibility visibility)
    {
        this.visibility = visibility;
    }

    /**
     * @return the time when this event ends
     */
    public LocalDateTime getEndTime()
    {
        return endTime;
    }

    /**
     * @param endTime the time when this event ends
     *        must be later than startTime
     */
    public void setEndTime(LocalDateTime endTime)
    {
        this.endTime = endTime;
    }

    /**
     * @return the time when this event starts
     */
    public LocalDateTime getStartTime()
    {
        return startTime;
    }

    /**
     * @param startTime the time when this event starts
     *        must be earlier than endTime
     */
    public void setStartTime(LocalDateTime startTime)
    {
        this.startTime = startTime;
    }

    /**
     * @return short description of this event
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description short description of this event
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return this event's title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title this event's title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return identifier of the event
     */
    public Long getId()
    {
        return id;
    }

    /**
     * @return User that owns this event
     */
    @JsonIgnore
    public User getUser()
    {
        return user;
    }

    /**
     * @param user of this event
     */
    public void setUser(User user)
    {
        this.user = user;
    }

    /**
     * @return the ticket sets created for this event
     */
    public Collection<TicketSet> getTicketSets()
    {
        //return Collections.unmodifiableCollection(ticketSets);
        return ticketSets;
    }

    /**
     * @param ticketSet new ticket set
     */
    public void addTicketSet(TicketSet ticketSet)
    {
        ticketSets.add(ticketSet);
        ticketSet.setEvent(this);
    }

    /**
     * @param ticketSet to be removed
     */
    public void removeTicketSet(TicketSet ticketSet)
    {
        ticketSets.remove(ticketSet);
    }

    /**
     * @return true if the event is canceled. Default to false
     */
    public boolean isCanceled()
    {
        return canceled;
    }

    /**
     * Set canceled state of event
     * @param canceled
     */
    public void setCanceled(boolean canceled)
    {
        this.canceled = canceled;
    }

    /**
     * Takes over attributes of other event (except ID and Owner)
     * @param other
     * @return a new event with ID and Owner of this instance and other attributes of other event
     */
    public Event merge(Event other)
    {
        Event event = new Event(other.title, other.description, other.startTime, other.endTime, other.visibility, other.canceled, null);

        event.id = this.id;

        event.user = this.user;

        return event;
    }

    /**
     * @return is this a past event
     */
    public boolean isExpired()
    {
        return LocalDateTime.now().isAfter(endTime);
    }

    /**
     * @return is this event happening
     */
    public boolean isHappening()
    {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }

    public Date getCreatedTime()
    {
        return createdTime;
    }

    public Date getUpdatedTime()
    {
        return updatedTime;
    }

    /**
     * Possible visibilities of an event
     */
    public static enum Visibility
    {
        PUBLIC, PRIVATE
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (title != null ? !title.equals(event.title) : event.title != null) return false;
        if (description != null ? !description.equals(event.description) : event.description != null) return false;
        if (startTime != null ? !startTime.equals(event.startTime) : event.startTime != null) return false;
        if (endTime != null ? !endTime.equals(event.endTime) : event.endTime != null) return false;
        if (canceled != event.canceled) return false;
        return visibility == event.visibility;

    }
}
