package app.web;

import app.data.*;
import app.data.Event.Visibility;
import app.data.validation.EventValidator;
import app.supports.converter.EnumConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/users/{userId}/events")
public class EventController
{
    private EventRepository eventRepository;

    private UserRepository userRepository;

    private EventValidator validator;

    private TicketSetRepository ticketSetRepository;

    /**
     * @param eventRepository Manage Event entities
     * @param validator       performs validation on Event entity
     */
    @Autowired
    public EventController(EventRepository eventRepository, UserRepository userRepository, TicketSetRepository ticketSetRepository, EventValidator validator)
    {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.ticketSetRepository = ticketSetRepository;
        this.validator = validator;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        binder.registerCustomEditor(Visibility.class, new EnumConverter(Visibility.class));
    }

    /**
     * @param requestEvent
     * @param bindingResult
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@PathVariable Long userId, Event requestEvent, BindingResult bindingResult)
    {
        HttpHeaders headers = new HttpHeaders();

        HttpStatus status = HttpStatus.CREATED;

        User user = userRepository.findById(userId);

        if (user != null) {

            validator.validate(requestEvent, bindingResult);

            if (!bindingResult.hasFieldErrors()) {

                requestEvent.setUser(user);

                Event event = eventRepository.saveOrUpdate(requestEvent);

                headers.setLocation(URI.create("/users/" + userId + "/events/" + event.getId()));
            } else {
                status = HttpStatus.BAD_REQUEST;
            }

        } else {
            status = HttpStatus.NOT_FOUND;
        }

        return new ResponseEntity(bindingResult.getFieldErrors(), headers, status);
    }

    /**
     * @param requestEvent
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/{eventId}", method = RequestMethod.PUT)
    public ResponseEntity update(@PathVariable Long userId, @PathVariable Long eventId, Event requestEvent, BindingResult bindingResult)
    {
        Event event = eventRepository.findByIdAndUserId(userId, eventId);

        // no event with the given eventId belongs to user with userId found
        if (event == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.NO_CONTENT;

        validator.validate(requestEvent, bindingResult);

        if (!bindingResult.hasFieldErrors()) {

            Event updatedEvent = eventRepository.saveOrUpdate(event.merge(requestEvent));

            headers.setLocation(URI.create("/users/" + userId + "/events/" + updatedEvent.getId()));
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity(bindingResult.getFieldErrors(), headers, status);
    }

    @RequestMapping(value = "/{eventId}", method = RequestMethod.GET)
    public ResponseEntity show(@PathVariable Long userId, @PathVariable Long eventId)
    {
        Event event = eventRepository.findByIdAndUserId(userId, eventId);

        HttpStatus status = HttpStatus.OK;

        if (event == null)
            status = HttpStatus.NOT_FOUND;

        return new ResponseEntity(event, status);
    }

    @RequestMapping(value = "/{eventId}/ticket-sets", method = RequestMethod.POST)
    public ResponseEntity addItem(@PathVariable Long userId, @PathVariable Long eventId, @Valid TicketSet ticketSet, BindingResult bindingResult)
    {
        Event event = eventRepository.findByIdAndUserId(userId, eventId);

        if (event == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (bindingResult.hasFieldErrors())
            return new ResponseEntity(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);

        event.addTicketSet(ticketSet);

        eventRepository.saveOrUpdate(event);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{eventId}/ticket-sets/{ticketSetId}", method = RequestMethod.PUT)
    public ResponseEntity updateTicketSet(@PathVariable long userId, @PathVariable long eventId,
                                          @PathVariable long ticketSetId, @Valid TicketSet updatedTicketSet, BindingResult bindingResult)
    {
        TicketSet ticketSet = ticketSetRepository.findByIdAndUserIdAndEventId(ticketSetId, userId, eventId);

        if (ticketSet == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (bindingResult.hasFieldErrors())
            return new ResponseEntity(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);

        ticketSetRepository.saveOrUpdate(ticketSet.merge(updatedTicketSet));

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
