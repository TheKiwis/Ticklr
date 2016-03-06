package app.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.cglib.core.Local;

import javax.persistence.*;
import java.time.LocalDateTime;

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
        this("New Event", "", null, null, Visibility.PRIVATE, false);

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
     */
    public Event(String title, String description, LocalDateTime startTime, LocalDateTime endTime, Visibility visibility, boolean canceled)
    {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.visibility = visibility;
        this.canceled = canceled;
    }

    public Visibility getVisibility()
    {
        return visibility;
    }

    public void setVisibility(Visibility visibility)
    {
        this.visibility = visibility;
    }

    public LocalDateTime getEndTime()
    {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime)
    {
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime()
    {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime)
    {
        this.startTime = startTime;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Long getId()
    {
        return id;
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
     * Takes over attributes of other event (except ID)
     * @param other
     * @return a new event with ID of this instance and other attributes of other event
     */
    public Event merge(Event other)
    {
        Event event = new Event(other.title, other.description, other.startTime, other.endTime, other.visibility, other.canceled);

        event.id = this.id;

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
