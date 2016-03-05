package app.web;

import app.data.Event;
import app.data.Event.Visibility;
import app.data.Event.Status;
import app.data.EventRepository;
import com.sun.beans.editors.EnumEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.net.URI;

@RestController
@RequestMapping("/events")
public class EventController
{
    private EventRepository eventRepository;

    /**
     * @param eventRepository
     */
    @Autowired
    public EventController(EventRepository eventRepository)
    {
        this.eventRepository = eventRepository;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        binder.registerCustomEditor(Visibility.class, new EnumEditor(Visibility.class));
        binder.registerCustomEditor(Status.class, new EnumEditor(Status.class));
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

        if (!bindingResult.hasFieldErrors()) {
            Event event = eventRepository.save(requestEvent);
            headers.setLocation(URI.create("/events/" + event.getId()));
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
