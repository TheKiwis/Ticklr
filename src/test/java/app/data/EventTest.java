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

        LocalDateTime expectedStart = LocalDateTime.now().plusDays(7);
        LocalDateTime expectedEnd = expectedStart.plusHours(1);
        LocalDateTime startTime = event.getStartTime();
        LocalDateTime endTime = event.getEndTime();

        assertEquals(expectedStart.getYear(), startTime.getYear());
        assertEquals(expectedStart.getMonth(), startTime.getMonth());
        assertEquals(expectedStart.getDayOfMonth(), startTime.getDayOfMonth());
        assertEquals(expectedEnd.getYear(), endTime.getYear());
        assertEquals(expectedEnd.getMonth(), endTime.getMonth());
        assertEquals(expectedEnd.getDayOfMonth(), endTime.getDayOfMonth());
    }
}