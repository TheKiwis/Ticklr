package app.supports.converter;

import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

/**
 * @author ngnmhieu
 */
public class ZonedDateTimeConverterTest
{
    @Test
    public void testConvert() throws Exception
    {
        ZonedDateTime expected = ZonedDateTime.of(2016, 3, 30, 8, 00, 00, 00, ZoneId.of("Z"));

        Converter<String, ZonedDateTime> converter = new ZonedDateTimeConverter(DateTimeFormatter.ISO_DATE_TIME);
        ZonedDateTime result = converter.convert("2016-03-30T10:00:00.000+02:00");

        assertEquals(expected, result);

        assertNull(converter.convert(""));
        assertNull(converter.convert(null));
    }
}