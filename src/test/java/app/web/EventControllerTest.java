package app.web;

import app.data.Event;
import app.data.EventRepository;
import app.web.forms.EventForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

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
    BindingResult bindingResult;

    @Before
    public void setUp()
    {
        controller = new EventController(eventRepository);
    }

    @Test
    public void create_shouldReturnHttpStatusCreated() throws Exception
    {
        EventForm mockForm = mock(EventForm.class);
        Event mockEvent = mock(Event.class);
        when(eventRepository.save(any())).thenReturn(mockEvent);

        ResponseEntity response = controller.create(mockForm, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void create_shouldCreateEventViaEventRepository() throws Exception
    {
        // mocks
        EventForm mockForm = mock(EventForm.class);
        Event mockEvent = mock(Event.class);
        when(mockForm.getEvent()).thenReturn(mockEvent);
        when(eventRepository.save(any())).thenReturn(mockEvent);

        // test object
        controller.create(mockForm, bindingResult);
        verify(eventRepository, times(1)).save(mockEvent);
    }

    //@Test
    // todo integration test for this
    //public void create_shouldReturnHttpStatusBadRequest() throws Exception
    //{
    //    // mocks
    //    EventForm mockForm = mock(EventForm.class);
    //    when(bindingResult.hasErrors()).thenReturn(true);
    //    when(bindingResult.hasFieldErrors()).thenReturn(true);
    //
    //    // test object
    //    ResponseEntity response = controller.create(mockForm, bindingResult);
    //    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //    // assert response body
    //}


    @Test
    public void show_shouldReturnHttpStatusOKwithEventInfo() throws Exception
    {
        Event mockEvent = mock(Event.class);
        when(eventRepository.findById(123)).thenReturn(mockEvent);

        // test object
        ResponseEntity response = controller.show(123);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEvent, response.getBody());
    }

    @Test
    public void show_shouldReturnHttpStatusNOT_FOUND() throws Exception
    {
        when(eventRepository.findById(123)).thenReturn(null);

        // test object
        ResponseEntity response = controller.show(123);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}