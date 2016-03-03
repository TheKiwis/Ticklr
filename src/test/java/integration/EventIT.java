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

    //@Test
    //public void shouldCreateEmptyEventWithDefaultValues() throws Exception
    //{
    //    MvcResult response = mockMvc.perform(post("/events")).andReturn();
    //
    //    Calendar cal = new GregorianCalendar();
    //    cal.add(Calendar.DATE, 7);
    //    String aWeekFromNow = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    //
    //    String location = response.getResponse().getHeader("Location");
    //    mockMvc.perform(get(location))
    //            .andExpect(status().isOk())
    //            .andExpect(jsonPath("$.id").isNotEmpty())
    //            .andExpect(jsonPath("$.title").value("New Event"))
    //            .andExpect(jsonPath("$.description").value(""))
    //            //.andExpect(jsonPath("$.startTime").value(startsWith((aWeekFromNow))))
    //            //.andExpect(jsonPath("$.endTime").value(startsWith(aWeekFromNow)))
    //            .andExpect(jsonPath("$.status").value("DRAFT"))
    //            .andExpect(jsonPath("$.visibility").value("PRIVATE"));
    //}

    @Test
    public void shouldReturnAnEvent() throws Exception
    {
        Calendar cal = new GregorianCalendar(2015, 6, 30);
        String expectedStartTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(cal.getTime());
        cal = new GregorianCalendar(2015, 7, 1);
        String expectedEndTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(cal.getTime());

        mockMvc.perform(get("/events/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.title").value("Sample Event"))
                .andExpect(jsonPath("$.description").value("Pool Party"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.visibility").value("PRIVATE"))
                .andExpect(jsonPath("$.start_time").value(expectedStartTime))
                .andExpect(jsonPath("$.end_time").value(expectedEndTime));
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
