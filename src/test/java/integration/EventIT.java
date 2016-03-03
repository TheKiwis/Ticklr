package integration;

import static org.hamcrest.CoreMatchers.*;

import app.data.Event;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Test;

import org.springframework.test.web.servlet.MvcResult;

import java.text.SimpleDateFormat;
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

        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, 7);
        String aWeekFromNow = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("New Event"))
                .andExpect(jsonPath("$.description").value(""))
                //.andExpect(jsonPath("$.startTime").value(startsWith((aWeekFromNow))))
                //.andExpect(jsonPath("$.endTime").value(startsWith(aWeekFromNow)))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.visibility").value("PRIVATE"));
    }

    // create with provided value

    @Test
    public void shouldReturnAnEvent() throws Exception
    {
        mockMvc.perform(get("/events/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.title").value("Sample Event"))
                .andExpect(jsonPath("$.description").value("Pool Party"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                //.andExpect(jsonPath("$.start_time").value())
                //.andExpect(jsonPath("$.end_time").value())
                .andExpect(jsonPath("$.visibility").value("PRIVATE"));
    }

    @Test
    public void shouldLoadTestFixture() throws Exception
    {
        Event u = (Event) em.createQuery("SELECT e FROM Event e WHERE e.id = 123").getSingleResult();
        assertEquals("Sample Event", u.getTitle());
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

    // todo startTime and endTime

    // todo invalid startTime and endTime

    // todo invalid status and visibility


    @Override
    protected IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("/fixtures/events_dataset.xml"));
    }
}
