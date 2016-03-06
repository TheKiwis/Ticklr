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
        assertEquals(null, event.getId());
        assertEquals("New Event", event.getTitle());
        assertEquals("", event.getDescription());
        assertEquals(Event.Visibility.PRIVATE, event.getVisibility());
        assertEquals(false, event.isCanceled());

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

    @Test
    public void testIsExpired() throws Exception
    {
        Event event = new Event();

        // before event
        event.setStartTime(LocalDateTime.now().plusDays(1));
        event.setEndTime(event.getStartTime().plusHours(1));
        assertFalse(event.isExpired());

        // after event
        event.setStartTime(LocalDateTime.now().minusDays(1));
        event.setEndTime(event.getStartTime().plusHours(1));
        assertTrue(event.isExpired());

        // while event is happening
        event.setStartTime(LocalDateTime.now().minusHours(1));
        event.setEndTime(event.getStartTime().plusHours(2));
        assertFalse(event.isExpired());
    }

    @Test
    public void testIsHappening() throws Exception
    {
        Event event = new Event();

        // before event
        event.setStartTime(LocalDateTime.now().plusDays(1));
        event.setEndTime(event.getStartTime().plusHours(1));
        assertFalse(event.isHappening());

        // after event
        event.setStartTime(LocalDateTime.now().minusDays(1));
        event.setEndTime(event.getStartTime().plusHours(1));
        assertFalse(event.isHappening());

        // while event is happening
        event.setStartTime(LocalDateTime.now().minusHours(1));
        event.setEndTime(event.getStartTime().plusHours(2));
        assertTrue(event.isHappening());
    }
}