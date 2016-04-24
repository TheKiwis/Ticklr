package app.data.validation;

import app.data.Event;
import app.data.User;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author ngnmhieu
 */
public class EventValidatorTest
{
    @Test
    public void testSupports() throws Exception
    {
        assertTrue(new EventValidator().supports(Event.class));
        assertFalse(new EventValidator().supports(User.class));
    }

    @Test
    public void testValidate() throws Exception
    {
        Event mockEvent = mock(Event.class);
        ZonedDateTime sdt = ZonedDateTime.of(2015, 10, 10, 12, 00, 00, 00, ZoneId.of("Z"));
        ZonedDateTime edt = ZonedDateTime.of(2015, 10, 10, 10, 00, 00, 00, ZoneId.of("Z"));
        when(mockEvent.getStartTime()).thenReturn(sdt);
        when(mockEvent.getEndTime()).thenReturn(edt);
        Errors result = mock(BindingResult.class);

        new EventValidator().validate(mockEvent, result);
        verify(result, atLeastOnce()).rejectValue(any(), any());
    }
}