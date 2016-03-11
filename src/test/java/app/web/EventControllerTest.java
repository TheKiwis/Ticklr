package app.web;

import app.data.*;
import app.data.validation.EventValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
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
    TicketSetRepository ticketSetRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BindingResult bindingResult;

    @Mock
    EventValidator validator;

    @Mock
    Event mockEvent;

    private long ticketSetId = 10l;
    private long userId = 1l;
    private long eventId = 123l;

    @Before
    public void setUp()
    {
        controller = new EventController(eventRepository, userRepository, ticketSetRepository, validator);
    }

    @Test
    public void create_shouldReturnHttpStatusCreated() throws Exception
    {
        // mocks
        List fieldErrors = mock(ArrayList.class);
        when(eventRepository.saveOrUpdate(any())).thenReturn(mockEvent);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        when(userRepository.findById(userId)).thenReturn(mock(User.class));

        // test object
        ResponseEntity response = controller.create(userId, mockEvent, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(fieldErrors, response.getBody());
    }

    @Test
    public void create_shouldCreateEventViaEventRepository() throws Exception
    {
        // mocks
        when(eventRepository.saveOrUpdate(any())).thenReturn(mockEvent);
        when(userRepository.findById(userId)).thenReturn(mock(User.class));

        // test object
        controller.create(userId, mockEvent, bindingResult);
        verify(eventRepository, times(1)).saveOrUpdate(mockEvent);
    }

    @Test
    public void create_shouldReturnHttpStatusBadRequest() throws Exception
    {
        // mocks
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(mock(User.class));

        // test object
        ResponseEntity response = controller.create(userId, mockEvent, bindingResult);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void create_shouldReturnHttpStatusNotFound() throws Exception
    {
        // mocks
        when(userRepository.findById(userId)).thenReturn(null);

        // test object
        ResponseEntity response = controller.create(userId, mockEvent, bindingResult);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // todo create return not found if user not found

    @Test
    public void show_shouldReturnHttpStatusNotFoundIfUserNotFound() throws Exception
    {
        when(eventRepository.findByIdAndUserId(userId, eventId)).thenReturn(null);

        // test object
        ResponseEntity response = controller.show(userId, eventId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void show_shouldReturnHttpStatusOKwithEventInfo() throws Exception
    {
        when(eventRepository.findByIdAndUserId(userId, eventId)).thenReturn(mockEvent);

        // test object
        ResponseEntity response = controller.show(userId, eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEvent, response.getBody());
    }

    @Test
    public void show_shouldReturnHttpStatusNotFound() throws Exception
    {
        when(eventRepository.findByIdAndUserId(userId, eventId)).thenReturn(null);

        // test object
        ResponseEntity response = controller.show(userId, eventId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void addTicketSet_shouldReturnHttpStatusCreated() throws Exception
    {
        TicketSet ticketSet = new TicketSet("Sample Ticket-set", new BigDecimal(25.00));

        when(eventRepository.findByIdAndUserId(userId, eventId)).thenReturn(mockEvent);

        ResponseEntity response = controller.addTicketSet(userId, eventId, ticketSet, bindingResult);

        verify(mockEvent, times(1)).addTicketSet(ticketSet);

        verify(eventRepository, times(1)).saveOrUpdate(mockEvent);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void addTicketSet_shouldReturnHttpStatusNotFound() throws Exception
    {
        TicketSet ticketSet = new TicketSet("Sample Ticket-set", new BigDecimal(25.00));

        when(eventRepository.findByIdAndUserId(userId, eventId)).thenReturn(null);

        ResponseEntity response = controller.addTicketSet(userId, eventId, ticketSet, bindingResult);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateTicketSet_shouldReturnHttpStatusNoContent() throws Exception
    {
        TicketSet ticketSet = new TicketSet("Updated Title", new BigDecimal(30.00));

        when(ticketSetRepository.findByIdAndUserIdAndEventId(ticketSetId, userId, eventId)).thenReturn(mock(TicketSet.class));

        ResponseEntity response = controller.updateTicketSet(userId, eventId, ticketSetId, ticketSet, bindingResult);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void deleteTicketSet_shouldReturnHttpStatusOk() throws Exception
    {
        TicketSet mockTicketSet = mock(TicketSet.class);

        when(ticketSetRepository.findByIdAndUserIdAndEventId(ticketSetId, userId, eventId)).thenReturn(mockTicketSet);

        ResponseEntity response = controller.deleteTicketSet(userId, eventId, ticketSetId);

        verify(ticketSetRepository, atLeastOnce()).delete(mockTicketSet);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteTicketSet_shouldReturnHttpStatusNotFound() throws Exception
    {
        when(ticketSetRepository.findByIdAndUserIdAndEventId(ticketSetId, userId, eventId)).thenReturn(null);

        ResponseEntity response = controller.deleteTicketSet(userId, eventId, ticketSetId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}