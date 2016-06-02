package app.web.common;

import app.web.event.EventResponse;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import java.io.IOException;

/**
 * @author ngnmhieu
 * @since 02.06.16
 */
public class TestSerializer extends JsonSerializer<EventResponse>
{

    @Override
    public void serialize(EventResponse value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException
    {
    }
}
