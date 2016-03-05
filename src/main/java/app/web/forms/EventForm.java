package app.web.forms;

import app.data.Event;

import java.util.Calendar;

/**
 * @author ngnmhieu
 */
public class EventForm
{
    private long id;

    private String title;

    private String description;

    private Calendar startTime;

    private Calendar endTime;

    private Event.Status status;

    private Event.Visibility visibility;

    public Event getEvent()
    {
        //return new Event(title, description, startTime, endTime, status, visibility);
        return new Event();
    }
}
