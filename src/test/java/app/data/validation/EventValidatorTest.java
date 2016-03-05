package app.data.validation;

import app.data.Event;
import app.data.User;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

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
        when(mockEvent.getStartTime()).thenReturn(LocalDateTime.of(2015, 10, 10, 12, 00));
        when(mockEvent.getEndTime()).thenReturn(LocalDateTime.of(2015, 10, 10, 10, 00));
        Errors result = mock(BindingResult.class);

        new EventValidator().validate(mockEvent, result);
        verify(result, atLeastOnce()).rejectValue(any(), any());
    }
}