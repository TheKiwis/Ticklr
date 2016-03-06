package app.data;

import com.fasterxml.jackson.annotation.JsonFormat;

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
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime endTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    /**
     * Default Constructor assigns default values to fields
     *  - title = New Event
     *  - description is empty
     *  - startTime = now + 7 days
     *  - endTime = startTime + 1 hours
     *  - status = DRAFT
     *  - visibility = PRIVATE
     */
    public Event()
    {
        this("New Event", "", null, null, Status.DRAFT, Visibility.PRIVATE);

        setStartTime(LocalDateTime.now().plusDays(7));
        setEndTime(startTime.plusHours(1));
    }

    /**
     * @param title event's title
     * @param description event's description
     * @param startTime time when the event starts
     * @param endTime time when the event ends
     * @param status status of the event @see Status
     * @param visibility visibility of the event @see Visibility
     */
    public Event(String title, String description, LocalDateTime startTime, LocalDateTime endTime, Status status, Visibility visibility)
    {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.visibility = visibility;
    }

    public Visibility getVisibility()
    {
        return visibility;
    }

    public void setVisibility(Visibility visibility)
    {
        this.visibility = visibility;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
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
     * Takes over attributes of other event (except ID)
     * @param other
     * @return a new event with ID of this instance and other attributes of other event
     */
    public Event merge(Event other)
    {
        Event event = new Event(other.title, other.description, other.startTime, other.endTime, other.status, other.visibility);

        event.id = this.id;

        return event;
    }

    /**
     * Possible statuses of an event
     */
    public static enum Status
    {
        DRAFT, PUBLISHED, DELETED, CANCELED
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
        if (status != event.status) return false;
        return visibility == event.visibility;

    }
}
