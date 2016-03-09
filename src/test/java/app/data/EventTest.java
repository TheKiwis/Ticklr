package app.data;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
        assertNull(event.getUser());

        LocalDateTime expectedStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(7);
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

    @Test
    public void testAddTicketSet() throws Exception
    {
        TicketSet mockTicketSet = mock(TicketSet.class);

        Event event = new Event();

        int count = event.getTicketSets().size();

        event.addTicketSet(mockTicketSet);

        assertEquals(count + 1, event.getTicketSets().size());
        assertTrue(event.getTicketSets().contains(mockTicketSet));
        verify(mockTicketSet, atLeastOnce()).setEvent(event);
    }
}