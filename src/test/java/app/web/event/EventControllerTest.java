package app.web.event;

import app.data.*;
import app.data.validation.EventValidator;
import app.services.EventRepository;
import app.services.TicketSetRepository;
import app.services.UserRepository;
import app.web.authorization.UserAuthorizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Event mockEvent;

    @Mock
    User mockUser;

    @Mock
    UserAuthorizer userAuthorizer;

    private Long ticketSetId = 10l;
    private UUID userId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");
    private Long eventId = 123l;

    @Before
    public void setUp()
    {
        // always authorized
        when(userAuthorizer.authorize(any())).thenReturn(true);
        controller = new EventController(eventRepository, userRepository, ticketSetRepository, validator, userAuthorizer, new EventURI("http://localhost"));

        // mockEvent belongs to User with userId
        when(mockEvent.getUser().getId()).thenReturn(userId);

        when(mockUser.getId()).thenReturn(userId);
    }

    @Test
    public void createEvent_shouldReturnHttpStatusCreated() throws Exception
    {
        // mocks
        when(userRepository.findById(userId)).thenReturn(mockUser);
        when(eventRepository.saveOrUpdate(any())).thenReturn(mockEvent);

        List fieldErrors = mock(ArrayList.class);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // test object
        ResponseEntity response = controller.createEvent(userId, mockEvent, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(fieldErrors, response.getBody());
    }

    @Test
    public void createEvent_shouldCreateEventViaEventRepository() throws Exception
    {
        // mocks
        when(userRepository.findById(userId)).thenReturn(mockUser);
        when(eventRepository.saveOrUpdate(any())).thenReturn(mockEvent);

        // test object
        controller.createEvent(userId, mockEvent, bindingResult);
        verify(eventRepository, times(1)).saveOrUpdate(mockEvent);
    }

    @Test
    public void createEvent_shouldReturnHttpStatusBadRequest() throws Exception
    {
        // mocks
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(mockUser);

        // test object
        ResponseEntity response = controller.createEvent(userId, mockEvent, bindingResult);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void createEvent_shouldReturnHttpStatusNotFoundIfUserNotFound() throws Exception
    {
        when(userRepository.findById(userId)).thenReturn(null);

        ResponseEntity response = controller.createEvent(userId, mockEvent, bindingResult);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void showEvent_shouldReturnHttpStatusNotFoundIfUserNotFound() throws Exception
    {
        when(userRepository.findById(userId)).thenReturn(null);

        ResponseEntity response = controller.showEvent(userId, eventId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void showEvent_shouldReturnHttpStatusOkWithEventInfo() throws Exception
    {
        when(userRepository.findById(userId)).thenReturn(mockUser);
        when(eventRepository.findById(eventId)).thenReturn(mockEvent);
        when(mockEvent.getId()).thenReturn(1l);

        ResponseEntity response = controller.showEvent(userId, eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Long.valueOf(1l), ((EventResponse)response.getBody()).id);
    }

    @Test
    public void showEvent_shouldReturnHttpStatusNotFound() throws Exception
    {
        when(userRepository.findById(userId)).thenReturn(mockUser);
        when(eventRepository.findById(eventId)).thenReturn(null);

        ResponseEntity response = controller.showEvent(userId, eventId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void addTicketSet_shouldReturnHttpStatusCreated() throws Exception
    {
        TicketSet ticketSet = new TicketSet("Sample Ticket-set", new BigDecimal(25.00));

        when(userRepository.findById(userId)).thenReturn(mockUser);
        when(eventRepository.findById(eventId)).thenReturn(mockEvent);

        ResponseEntity response = controller.addTicketSet(userId, eventId, ticketSet, bindingResult);

        verify(mockEvent, times(1)).addTicketSet(ticketSet);
        verify(eventRepository, times(1)).saveOrUpdate(mockEvent);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void addTicketSet_shouldReturnHttpStatusNotFound() throws Exception
    {
        TicketSet ticketSet = new TicketSet("Sample Ticket-set", new BigDecimal(25.00));

        when(userRepository.findById(userId)).thenReturn(mockUser);
        when(eventRepository.findById(eventId)).thenReturn(null);

        ResponseEntity response = controller.addTicketSet(userId, eventId, ticketSet, bindingResult);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateTicketSet_shouldReturnHttpStatusNoContent() throws Exception
    {
        TicketSet ticketSet = new TicketSet("Updated Title", new BigDecimal(30.00));

        TicketSet foundTicketSet = mock(TicketSet.class, RETURNS_DEEP_STUBS);
        when(ticketSetRepository.findById(ticketSetId)).thenReturn(foundTicketSet);
        when(foundTicketSet.getEvent().getId()).thenReturn(eventId);
        when(foundTicketSet.getEvent().getUser().getId()).thenReturn(userId);

        ResponseEntity response = controller.updateTicketSet(userId, eventId, ticketSetId, ticketSet, bindingResult);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void deleteTicketSet_shouldReturnHttpStatusOk() throws Exception
    {
        TicketSet foundTicketSet = mock(TicketSet.class, RETURNS_DEEP_STUBS);
        when(ticketSetRepository.findById(ticketSetId)).thenReturn(foundTicketSet);
        when(foundTicketSet.getEvent().getId()).thenReturn(eventId);
        when(foundTicketSet.getEvent().getUser().getId()).thenReturn(userId);

        ResponseEntity response = controller.deleteTicketSet(userId, eventId, ticketSetId);

        verify(ticketSetRepository, atLeastOnce()).delete(foundTicketSet);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteTicketSet_shouldReturnHttpStatusNotFound() throws Exception
    {
        when(ticketSetRepository.findById(ticketSetId)).thenReturn(null);

        ResponseEntity response = controller.deleteTicketSet(userId, eventId, ticketSetId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}