package app.supports.converter;

import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

/**
 * @author ngnmhieu
 */
public class LocalDateTimeConverterTest
{
    @Test
    public void testConvert() throws Exception
    {
        LocalDateTime expected = LocalDateTime.of(2016, Month.MARCH, 30, 8, 30, 45);

        Converter<String, LocalDateTime> converter = new LocalDateTimeConverter(DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime result = converter.convert("2016-03-30T08:30:45");

        assertEquals(expected.getYear(), result.getYear());
        assertEquals(expected.getMonth(), result.getMonth());
        assertEquals(expected.getDayOfMonth(), result.getDayOfMonth());
        assertEquals(expected.getHour(), result.getHour());
        assertEquals(expected.getMinute(), result.getMinute());
        assertEquals(expected.getSecond(), result.getSecond());

        assertNull(converter.convert(""));
        assertNull(converter.convert(null));
    }
}