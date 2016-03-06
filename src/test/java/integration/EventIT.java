package integration;

import static org.hamcrest.CoreMatchers.*;

import app.data.Event;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import org.springframework.test.web.servlet.MvcResult;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author ngnmhieu
 */
public class EventIT extends CommonIntegrationTest
{
    private Event preloadedEvent;
    private String sampleTitle;
    private String sampleDesc;
    private String sampleVisibility;
    private String sampleStartTime;
    private String sampleEndTime;
    private Boolean sampleCanceled;

    @Before
    public void sampleInput()
    {
        preloadedEvent = (Event) em.createQuery("SELECT e FROM Event e WHERE e.id = 123").getSingleResult();

        sampleTitle = preloadedEvent.getTitle();
        sampleDesc = preloadedEvent.getDescription();
        sampleVisibility = preloadedEvent.getVisibility().toString();
        sampleStartTime = preloadedEvent.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        sampleEndTime = preloadedEvent.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        sampleCanceled = preloadedEvent.isCanceled();
    }


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
        MvcResult response = mockMvc.perform(post("/events")).andExpect(status().isCreated()).andReturn();

        String aWeekFromNow = LocalDateTime.now().plusDays(7).withNano(0).format(DateTimeFormatter.ISO_DATE);

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("New Event"))
                .andExpect(jsonPath("$.description").value(""))
                .andExpect(jsonPath("$.canceled").value(false))
                .andExpect(jsonPath("$.visibility").value("PRIVATE"))
                .andExpect(jsonPath("$.startTime").value(startsWith((aWeekFromNow))))
                .andExpect(jsonPath("$.endTime").value(startsWith(aWeekFromNow)))
                .andExpect(jsonPath("$.expired").value(false))
                .andExpect(jsonPath("$.happening").value(false));
    }

    @Test
    public void shouldCreateAnEventWithProvidedValue() throws Exception
    {
        MvcResult response = mockMvc.perform(post("/events")
                .param("title", sampleTitle)
                .param("description", sampleDesc)
                .param("startTime", sampleStartTime)
                .param("endTime", sampleEndTime)
                .param("canceled", "true")
                .param("visibility", sampleVisibility)
        ).andExpect(status().isCreated()).andReturn();

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(sampleTitle))
                .andExpect(jsonPath("$.description").value(sampleDesc))
                .andExpect(jsonPath("$.startTime").value(startsWith(sampleStartTime)))
                .andExpect(jsonPath("$.endTime").value(startsWith(sampleEndTime)))
                .andExpect(jsonPath("$.canceled").value(sampleCanceled))
                .andExpect(jsonPath("$.visibility").value(sampleVisibility));
    }

    @Test
    public void shouldUpdateAnExistingEvent() throws Exception
    {
        MvcResult response = mockMvc.perform(put("/events/123")
                .param("title", sampleTitle)
                .param("description", sampleDesc)
                .param("startTime", sampleStartTime)
                .param("endTime", sampleEndTime)
                .param("canceled", sampleCanceled ? "true" : "false")
                .param("visibility", sampleVisibility)
        ).andExpect(status().isNoContent()).andReturn();

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(sampleTitle))
                .andExpect(jsonPath("$.description").value(sampleDesc))
                .andExpect(jsonPath("$.startTime").value(startsWith(sampleStartTime)))
                .andExpect(jsonPath("$.endTime").value(startsWith(sampleEndTime)))
                .andExpect(jsonPath("$.canceled").value(sampleCanceled))
                .andExpect(jsonPath("$.visibility").value(sampleVisibility));
    }

    @Test
    public void shouldCreateNewResourceIfUpdateCannotFindResource() throws Exception
    {
        // Event with ID 345 doesn't exist
        assertNull(em.find(Event.class, 345l));

        // put operation should create a new one (with a different ID)
        mockMvc.perform(put("/events/345")
                .param("title", sampleTitle)
                .param("description", sampleDesc)
                .param("startTime", sampleStartTime)
                .param("endTime", sampleEndTime)
                .param("visibility", sampleVisibility))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/events/")));

    }

    @Test
    public void shouldNotUpdateEventOnInvalidRequest() throws Exception
    {
        Event beforePut = em.find(Event.class, 123l);

        mockMvc.perform(put("/events/123")
                .param("title", "Title")
                .param("description", "Desc")
                .param("visibility", "Invalid Visibility"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());

        Event afterPut = em.find(Event.class, 123l);

        assertTrue(afterPut.equals(beforePut));
    }

    @Test
    public void shouldReturnAnEvent() throws Exception
    {
        mockMvc.perform(get("/events/" + preloadedEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(preloadedEvent.getId().intValue()))
                .andExpect(jsonPath("$.title").value(sampleTitle))
                .andExpect(jsonPath("$.description").value(sampleDesc))
                .andExpect(jsonPath("$.canceled").value(sampleCanceled))
                .andExpect(jsonPath("$.visibility").value(sampleVisibility))
                .andExpect(jsonPath("$.startTime").value(startsWith(sampleStartTime)))
                .andExpect(jsonPath("$.endTime").value(startsWith(sampleEndTime)));
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

    @Test
    public void shouldReturnBadRequestForInvalidStatusAndVisibility() throws Exception
    {
        mockMvc.perform(post("/events")
                .param("title", "Title")
                .param("description", "Desc")
                .param("visibility", "Invalid Visibility"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void shouldReturnBadRequestForMalformedTime() throws Exception
    {
        mockMvc.perform(post("/events")
                .param("title", "Title")
                .param("description", "Desc")
                .param("startTime", "2015-03-12"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());

        mockMvc.perform(post("/events")
                .param("title", "Title")
                .param("description", "Desc")
                .param("startTime", "Invalid time"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void shouldReturnBadRequestWhenEndTimeBeforeStartTime() throws Exception
    {
        mockMvc.perform(post("/events")
                .param("title", "Title")
                .param("description", "Desc")
                .param("startTime", "2015-03-12T20:00:00")
                .param("endTime", "2015-03-12T19:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void shouldLoadTestFixture() throws Exception
    {
        assertNotNull(em.createQuery("SELECT e FROM Event e WHERE e.id = 123").getSingleResult());
    }

    @Override
    protected IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("/fixtures/events_dataset.xml"));
    }
}
