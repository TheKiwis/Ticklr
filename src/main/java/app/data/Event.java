package app.data;

import javax.persistence.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author ngnmhieu
 */
@Entity
@Table(name = "events")
public class Event
{
    private static final int DEFAULT_START_DAYS_OFFSET = 7;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_time")
    private Calendar startTime;

    @Column(name = "end_time")
    private Calendar endTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    /**
     * Default Constructor assigns default values to fields
     */
    public Event()
    {
        this("New Event", "", null, null, Status.DRAFT, Visibility.PRIVATE);

        Calendar aWeekLater = new GregorianCalendar();
        aWeekLater.add(Calendar.DATE, DEFAULT_START_DAYS_OFFSET);
        setStartTime(aWeekLater);
        setEndTime(aWeekLater);
    }

    /**
     * @param title event's title
     * @param description event's description
     * @param startTime time when the event starts
     * @param endTime time when the event ends
     * @param status status of the event @see Status
     * @param visibility visibility of the event @see Visibility
     */
    public Event(String title, String description, Calendar startTime, Calendar endTime, Status status, Visibility visibility)
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

    public Calendar getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Calendar endTime)
    {
        this.endTime = endTime;
    }

    public Calendar getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Calendar startTime)
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

    public long getId()
    {
        return id;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (id != event.id) return false;

        return true;
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
}
