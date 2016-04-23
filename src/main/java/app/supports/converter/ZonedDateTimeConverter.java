package app.supports.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Parse and convert String into LocalDateTime object
 *
 * @author ngnmhieu
 */
public class ZonedDateTimeConverter implements Converter<String, ZonedDateTime>
{
    DateTimeFormatter formatter;

    public ZonedDateTimeConverter(DateTimeFormatter formatter)
    {
        this.formatter = formatter;
    }

    @Override
    /**
     * Convert the date specified in text into a ZonedDateTime object with ZoneId.of("Z")
     */
    public ZonedDateTime convert(String text)
    {
        if (text == null || text.isEmpty())
            return null;
        return ZonedDateTime.parse(text, formatter).withZoneSameInstant(ZoneId.of("Z"));
    }
}
