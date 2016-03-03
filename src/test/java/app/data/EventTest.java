package app.data;

import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * @author ngnmhieu
 */
public class EventTest
{
    @Test
    public void shouldHaveDefaultValues() throws Exception
    {
        Event event = new Event();
        assertEquals(0, event.getId());
        assertEquals("New Event", event.getTitle());
        assertEquals("", event.getDescription());
        assertEquals(Event.Visibility.PRIVATE, event.getVisibility());
        assertEquals(Event.Status.DRAFT, event.getStatus());

        Calendar startTime = event.getStartTime();
        Calendar endTime = event.getEndTime();

        Calendar expected = new GregorianCalendar();
        expected.add(Calendar.DATE, 7);

        assertEquals(expected.get(Calendar.YEAR),  startTime.get(Calendar.YEAR));
        assertEquals(expected.get(Calendar.MONTH), startTime.get(Calendar.MONTH));
        assertEquals(expected.get(Calendar.DATE),  startTime.get(Calendar.DATE));
        assertEquals(expected.get(Calendar.YEAR),  endTime.get(Calendar.YEAR));
        assertEquals(expected.get(Calendar.MONTH), endTime.get(Calendar.MONTH));
        assertEquals(expected.get(Calendar.DATE),  endTime.get(Calendar.DATE));
    }
}