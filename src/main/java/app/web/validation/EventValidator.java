package app.web.validation;


import app.data.event.Event;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author ngnmhieu
 */
@Component
public class EventValidator implements Validator
{
    @Override
    public boolean supports(Class<?> clazz)
    {
        return Event.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors)
    {
        Event event = (Event) target;

        if (event.getEndTime().isBefore(event.getStartTime())) {
            String errorCode = "EndTimeBeforeStartTime";
            errors.rejectValue("startTime", errorCode);
            errors.rejectValue("endTime", errorCode);
        }
    }
}
