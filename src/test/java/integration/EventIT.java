package integration;

import static org.hamcrest.CoreMatchers.*;

import app.data.Event;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Test;

import org.springframework.cglib.core.Local;
import org.springframework.test.web.servlet.MvcResult;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author ngnmhieu
 */
public class EventIT extends CommonIntegrationTest
{

    @Test
    public void shouldReturnWithHttpStatusCreatedAndALocationHeader() throws Exception
    {
        mockMvc.perform(post("/events"))
                .andExpect(header().string("Location", startsWith("/events")))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldCreateEmptyEventWithDefaultValues() throws Exception
    {
        MvcResult response = mockMvc.perform(post("/events")).andReturn();

        String aWeekFromNow = LocalDateTime.now().plusDays(7).withNano(0).format(DateTimeFormatter.ISO_DATE);

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("New Event"))
                .andExpect(jsonPath("$.description").value(""))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.visibility").value("PRIVATE"))
                .andExpect(jsonPath("$.startTime").value(startsWith((aWeekFromNow))))
                .andExpect(jsonPath("$.endTime").value(startsWith(aWeekFromNow)));
    }

    @Test
    public void shouldReturnAnEvent() throws Exception
    {
        String expectedStartTime = LocalDateTime.of(2015, Month.JUNE, 30, 0, 0, 0).format(DateTimeFormatter.ISO_DATE_TIME);
        String expectedEndTime = LocalDateTime.of(2015, Month.JULY, 1, 0, 0, 0).format(DateTimeFormatter.ISO_DATE_TIME);

        mockMvc.perform(get("/events/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.title").value("Sample Event"))
                .andExpect(jsonPath("$.description").value("Pool Party"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.visibility").value("PRIVATE"))
                .andExpect(jsonPath("$.startTime").value(startsWith(expectedStartTime)))
                .andExpect(jsonPath("$.endTime").value(startsWith(expectedEndTime)));
    }

    @Test
    public void shouldLoadTestFixture() throws Exception
    {
        Event u = (Event) em.createQuery("SELECT e FROM Event e WHERE e.id = 123").getSingleResult();
        assertEquals("Sample Event", u.getTitle());
        assertEquals("Pool Party", u.getDescription());
        assertEquals(Event.Visibility.PRIVATE, u.getVisibility());
        assertEquals(Event.Status.DRAFT, u.getStatus());

        LocalDateTime startTime = u.getStartTime();
        assertEquals(2015, startTime.getYear());
        assertEquals(Month.JUNE, startTime.getMonth());
        assertEquals(30, startTime.getDayOfMonth());

        LocalDateTime endTime = u.getEndTime();
        assertEquals(2015, endTime.getYear());
        assertEquals(Month.JULY, endTime.getMonth());
        assertEquals(1, endTime.getDayOfMonth());
    }

    @Test
    public void shouldReturnHttpStatusNotFoundForNonExistentEvent() throws Exception
    {
        mockMvc.perform(get("/events/555"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnHttpStatusBadRequestForNonNumericEventId() throws Exception
    {
        mockMvc.perform(get("/events/non_numeric"))
                .andExpect(status().isBadRequest());
    }

    // todo create with provided value

    // todo startTime and endTime

    // todo invalid startTime and endTime

    // todo invalid status and visibility


    @Override
    protected IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("/fixtures/events_dataset.xml"));
    }
}
