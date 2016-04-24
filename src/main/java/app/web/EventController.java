package app.web;

import app.data.*;
import app.data.Event.Visibility;
import app.data.validation.EventValidator;
import app.supports.converter.EnumConverter;
import app.web.authorization.UserAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/users/{userId}/events")
public class EventController
{
    private static final ResponseEntity NOT_FOUND = new ResponseEntity(HttpStatus.NOT_FOUND);
    private static final ResponseEntity FORBIDDEN = new ResponseEntity(HttpStatus.FORBIDDEN);
    private static final ResponseEntity BAD_REQUEST = new ResponseEntity(HttpStatus.BAD_REQUEST);

    private EventRepository eventRepository;

    private UserRepository userRepository;

    private EventValidator validator;

    private TicketSetRepository ticketSetRepository;

    private UserAuthorizer userAuthorizer;

    /**
     * @param eventRepository Manage Event entities
     * @param validator       performs validation on Event entity
     */
    @Autowired
    public EventController(EventRepository eventRepository, UserRepository userRepository,
                           TicketSetRepository ticketSetRepository, EventValidator validator, UserAuthorizer userAuthorizer)
    {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.ticketSetRepository = ticketSetRepository;
        this.validator = validator;
        this.userAuthorizer = userAuthorizer;
    }

    /**
     * @param requestEvent
     * @param bindingResult
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createEvent(@PathVariable Long userId, @RequestBody(required = false) Event requestEvent, BindingResult bindingResult)
    {
        User user = userRepository.findById(userId);

        if (user == null)
            return NOT_FOUND;

        if (!userAuthorizer.authorize(user))
            return FORBIDDEN;

        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.CREATED;

        if (requestEvent == null)
            requestEvent = new Event();

        validator.validate(requestEvent, bindingResult);

        if (!bindingResult.hasFieldErrors()) {

            requestEvent.setUser(user);

            Event event = eventRepository.saveOrUpdate(requestEvent);

            headers.setLocation(URI.create(eventUri(userId, event.getId())));
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity(bindingResult.getFieldErrors(), headers, status);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException(HttpMessageNotReadableException ex)
    {
        // todo more descriptive response
        return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    /**
     * @param requestEvent
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/{eventId}", method = RequestMethod.PUT)
    public ResponseEntity updateEvent(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody(required = false) Event requestEvent, BindingResult bindingResult)
    {
        Event event = eventRepository.findById(eventId);

        if (event == null)
            return NOT_FOUND;

        User user = event.getUser();
        if (!user.getId().equals(userId)) // event doesn't belong to user
            return NOT_FOUND;

        if (!userAuthorizer.authorize(user))
            return FORBIDDEN;

        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.NO_CONTENT;

        // todo consider let handleHttpMessageNotReadableException return Bad Request response with appropriate message
        if (requestEvent == null)
            return BAD_REQUEST;

        validator.validate(requestEvent, bindingResult);

        if (!bindingResult.hasFieldErrors()) {

            Event updatedEvent = eventRepository.saveOrUpdate(event.merge(requestEvent));

            headers.setLocation(URI.create(eventUri(userId, updatedEvent.getId())));
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity(bindingResult.getFieldErrors(), headers, status);
    }

    @RequestMapping(value = "/{eventId}", method = RequestMethod.GET)
    public ResponseEntity showEvent(@PathVariable Long userId, @PathVariable Long eventId)
    {
        User user = userRepository.findById(userId);

        if (user == null)
            return NOT_FOUND;

        Event event = eventRepository.findById(eventId);

        if (event == null || !event.getUser().getId().equals(userId))
            return NOT_FOUND;

        if (!userAuthorizer.authorize(user))
            return FORBIDDEN;

        return new ResponseEntity(event, HttpStatus.OK);
    }

    @RequestMapping(value = "/{eventId}", method = RequestMethod.DELETE)
    public ResponseEntity cancelEvent(@PathVariable Long userId, @PathVariable Long eventId)
    {
        User user = userRepository.findById(userId);

        if (user == null)
            return NOT_FOUND;

        Event event = eventRepository.findById(eventId);

        // no event with the given eventId belongs to user with userId found
        if (event == null || !event.getUser().getId().equals(userId))
            return NOT_FOUND;

        if (!userAuthorizer.authorize(user))
            return FORBIDDEN;

        event.setCanceled(true);

        eventRepository.saveOrUpdate(event);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{eventId}/ticket-sets", method = RequestMethod.POST)
    public ResponseEntity addTicketSet(@PathVariable Long userId, @PathVariable Long eventId, @Valid TicketSet ticketSet, BindingResult bindingResult)
    {
        User user = userRepository.findById(userId);

        if (user == null)
            return NOT_FOUND;

        Event event = eventRepository.findById(eventId);

        // no event with the given eventId belongs to user with userId found
        if (event == null || !event.getUser().getId().equals(userId))
            return NOT_FOUND;

        if (!userAuthorizer.authorize(user))
            return FORBIDDEN;

        if (bindingResult.hasFieldErrors())
            return new ResponseEntity(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);

        event.addTicketSet(ticketSet);

        eventRepository.saveOrUpdate(event);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(ticketSetUri(userId, event.getId(), ticketSet.getId())));

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{eventId}/ticket-sets/{ticketSetId}", method = RequestMethod.PUT)
    public ResponseEntity updateTicketSet(@PathVariable long userId, @PathVariable long eventId,
                                          @PathVariable long ticketSetId, @Valid TicketSet updatedTicketSet, BindingResult bindingResult)
    {
        TicketSet ticketSet = ticketSetRepository.findById(ticketSetId);

        if (ticketSet == null)
            return NOT_FOUND;

        Event event = ticketSet.getEvent();
        if (!event.getId().equals(eventId)) // ticketSet doesn't belong to event
            return NOT_FOUND;

        User user = event.getUser();
        if (!user.getId().equals(userId)) // event doesn't belong to user
            return NOT_FOUND;

        if (!userAuthorizer.authorize(user))
            return FORBIDDEN;

        if (bindingResult.hasFieldErrors())
            return new ResponseEntity(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);

        ticketSetRepository.saveOrUpdate(ticketSet.merge(updatedTicketSet));

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    // todo deals with situation when basket items still reference this ticket set
    @RequestMapping(value = "/{eventId}/ticket-sets/{ticketSetId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteTicketSet(@PathVariable long userId, @PathVariable long eventId, @PathVariable long ticketSetId)
    {
        TicketSet ticketSet = ticketSetRepository.findById(ticketSetId);

        if (ticketSet == null)
            return NOT_FOUND;

        Event event = ticketSet.getEvent();
        if (!event.getId().equals(eventId)) // ticketSet doesn't belong to event
            return NOT_FOUND;

        User user = event.getUser();
        if (!user.getId().equals(userId)) // event doesn't belong to user
            return NOT_FOUND;

        if (!userAuthorizer.authorize(user))
            return FORBIDDEN;

        ticketSetRepository.delete(ticketSet);

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * @param userId  != null
     * @param eventId if null, it's not included in the result
     * @return Event URI
     */
    private String eventUri(Long userId, Long eventId)
    {
        assert userId != null;
        return "/api/users/" + userId + "/events" + (eventId != null ? "/" + eventId : "");
    }

    /**
     * @param userId
     * @param eventId
     * @param ticketSetId
     * @return Ticket Set URI
     */
    private String ticketSetUri(Long userId, Long eventId, Long ticketSetId)
    {
        assert userId != null;
        assert eventId != null;
        return eventUri(userId, eventId) + "/ticket-sets" + (ticketSetId == null ? "" : "/" + ticketSetId);
    }
}
