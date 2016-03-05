package app.supports.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Parse and convert String into LocalDateTime object
 * @author ngnmhieu
 */
public class LocalDateTimeConverter implements Converter<String, LocalDateTime>
{
    DateTimeFormatter formatter;

    public LocalDateTimeConverter(DateTimeFormatter formatter)
    {
        this.formatter = formatter;
    }

    @Override
    public LocalDateTime convert(String text)
    {
        if (text == null || text.isEmpty())
            return null;

        return LocalDateTime.parse(text, formatter);
    }
}
