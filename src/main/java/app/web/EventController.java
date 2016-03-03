package app.web;

import app.data.Event;
import app.data.EventRepository;
import app.web.forms.EventForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     *
     * @param eventForm
     * @param bindingResult
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(EventForm eventForm, BindingResult bindingResult)
    {
        Event event = eventRepository.save(eventForm.getEvent());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/events/" + event.getId()));

        ResponseEntity response = new ResponseEntity("", headers, HttpStatus.CREATED);

        return response;
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
