package app.web;

import app.data.Event;
import app.data.EventRepository;
import app.data.User;
import app.data.UserRepository;
import app.data.validation.EventValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author ngnmhieu
 */
@RunWith(MockitoJUnitRunner.class)
public class EventControllerTest
{
    private EventController controller;

    @Mock
    EventRepository eventRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BindingResult bindingResult;

    @Mock
    EventValidator validator;

    @Before
    public void setUp()
    {
        controller = new EventController(eventRepository, userRepository, validator);
    }

    @Test
    public void create_shouldReturnHttpStatusCreated() throws Exception
    {
        // mocks
        Event mockEvent = mock(Event.class);
        List fieldErrors = mock(ArrayList.class);
        when(eventRepository.saveOrUpdate(any())).thenReturn(mockEvent);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        when(userRepository.findById(1l)).thenReturn(mock(User.class));

        // test object
        ResponseEntity response = controller.create(1l, mockEvent, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(fieldErrors, response.getBody());
    }

    @Test
    public void create_shouldCreateEventViaEventRepository() throws Exception
    {
        // mocks
        Event mockEvent = mock(Event.class);
        when(eventRepository.saveOrUpdate(any())).thenReturn(mockEvent);
        when(userRepository.findById(1l)).thenReturn(mock(User.class));

        // test object
        controller.create(1l, mockEvent, bindingResult);
        verify(eventRepository, times(1)).saveOrUpdate(mockEvent);
    }

    @Test
    public void create_shouldReturnHttpStatusBadRequest() throws Exception
    {
        // mocks
        Event mockEvent = mock(Event.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        when(userRepository.findById(1l)).thenReturn(mock(User.class));

        // test object
        ResponseEntity response = controller.create(1l, mockEvent, bindingResult);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void create_shouldReturnHttpStatusNotFound() throws Exception
    {
        // mocks
        Event mockEvent = mock(Event.class);
        when(userRepository.findById(1l)).thenReturn(null);

        // test object
        ResponseEntity response = controller.create(1l, mockEvent, bindingResult);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // todo create return not found if user not found

    @Test
    public void show_shouldReturnHttpStatusNotFoundIfUserNotFound() throws Exception
    {
        when(eventRepository.findByIdAndUserId(1l, 123l)).thenReturn(null);

        // test object
        ResponseEntity response = controller.show(1l, 123l);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void show_shouldReturnHttpStatusOKwithEventInfo() throws Exception
    {
        Event mockEvent = mock(Event.class);
        when(eventRepository.findByIdAndUserId(1l, 123l)).thenReturn(mockEvent);

        // test object
        ResponseEntity response = controller.show(1l, 123l);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEvent, response.getBody());
    }

    @Test
    public void show_shouldReturnHttpStatusNotFound() throws Exception
    {
        when(eventRepository.findByIdAndUserId(1l, 123l)).thenReturn(null);

        // test object
        ResponseEntity response = controller.show(1l, 123l);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}