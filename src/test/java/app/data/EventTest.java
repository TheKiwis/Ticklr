package app.data;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
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

        LocalDateTime expected = LocalDateTime.now().plusDays(7);
        LocalDateTime startTime = event.getStartTime();
        LocalDateTime endTime = event.getEndTime();

        assertEquals(expected.getYear(), startTime.getYear());
        assertEquals(expected.getMonth(), startTime.getMonth());
        assertEquals(expected.getDayOfMonth(), startTime.getDayOfMonth());
        assertEquals(expected.getYear(), endTime.getYear());
        assertEquals(expected.getMonth(), endTime.getMonth());
        assertEquals(expected.getDayOfMonth(), endTime.getDayOfMonth());
    }
}