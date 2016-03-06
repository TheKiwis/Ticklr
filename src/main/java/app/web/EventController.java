package app.web;

import app.data.Event;
import app.data.Event.Visibility;
import app.data.Event.Status;
import app.data.EventRepository;
import app.data.validation.EventValidator;
import app.supports.converter.EnumConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/events")
public class EventController
{
    private EventRepository eventRepository;

    private EventValidator validator;

    /**
     * @param eventRepository Manage Event entities
     * @param validator performs validation on Event entity
     */
    @Autowired
    public EventController(EventRepository eventRepository, EventValidator validator)
    {
        this.eventRepository = eventRepository;
        this.validator = validator;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        binder.registerCustomEditor(Visibility.class, new EnumConverter(Visibility.class));
        binder.registerCustomEditor(Status.class, new EnumConverter(Status.class));
    }

    /**
     * @param requestEvent
     * @param bindingResult
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(Event requestEvent, BindingResult bindingResult)
    {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.CREATED;

        validator.validate(requestEvent, bindingResult);

        if (!bindingResult.hasFieldErrors()) {
            Event event = eventRepository.saveOrUpdate(requestEvent);
            headers.setLocation(URI.create("/events/" + event.getId()));
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity(bindingResult.getFieldErrors(), headers, status);
    }

    /**
     * @param requestEvent
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/{eventId}", method = RequestMethod.PUT)
    public ResponseEntity update(@PathVariable Long eventId, Event requestEvent, BindingResult bindingResult)
    {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.NO_CONTENT;

        validator.validate(requestEvent, bindingResult);

        if (!bindingResult.hasFieldErrors()) {

            Event event = eventRepository.findById(eventId);

            // no event with the given eventId found, a new one should be created
            if (event == null)
                return create(requestEvent, bindingResult);

            // update existing event
            Event updatedEvent = eventRepository.saveOrUpdate(event.merge(requestEvent));

            headers.setLocation(URI.create("/events/" + updatedEvent.getId()));
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity(bindingResult.getFieldErrors(), headers, status);
    }

    @RequestMapping(value = "/{eventId}", method = RequestMethod.GET)
    public ResponseEntity show(@PathVariable long eventId)
    {
        Event event = eventRepository.findById(eventId);

        HttpStatus status = HttpStatus.OK;

        if (event == null)
            status = HttpStatus.NOT_FOUND;

        return new ResponseEntity(event, status);
    }


}
