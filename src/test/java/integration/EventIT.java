package integration;

import static org.hamcrest.CoreMatchers.*;

import app.data.Event;
import app.data.TicketSet;
import app.data.User;
import app.web.authentication.JwtAuthenticator;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author ngnmhieu
 */
public class EventIT extends CommonIntegrationTest
{
    // loaded fixtures from database
    private User sampleUser;
    private Event sampleEvent;

    private String sampleTitle;
    private String sampleDesc;
    private String sampleVisibility;
    private String sampleStartTime;
    private String sampleEndTime;
    private Boolean sampleCanceled;

    // Authentication token is put in Authorization header in each request
    private String loginString;

    @Value("${auth.secret}")
    private String authSecret;


    private String ticketSetUrl(int userId, int eventId, Integer ticketSetId)
    {
        return eventUrl(userId, eventId) + "/ticket-sets/" + (ticketSetId == null ? "" : ticketSetId);
    }

    private String eventUrl(int userId, Integer eventId)
    {
        return "/users/" + userId + "/events/" + (eventId != null ? eventId : "");
    }

    @Before
    public void login() throws Exception
    {
        loginString = "Bearer " + new JwtAuthenticator(authSecret).generateToken("user@example.com").getKey();
    }

    @Before
    public void sampleInput() throws Exception
    {
        sampleEvent = em.find(Event.class, 123l);
        sampleUser = sampleEvent.getUser();

        sampleTitle = sampleEvent.getTitle();
        sampleDesc = sampleEvent.getDescription();
        sampleVisibility = sampleEvent.getVisibility().toString();
        sampleStartTime = sampleEvent.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        sampleEndTime = sampleEvent.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        sampleCanceled = sampleEvent.isCanceled();
    }

    /*************************************************
     * Happy Path
     *************************************************/

    @Test
    public void shouldReturnAnEvent() throws Exception
    {
        mockMvc.perform(get(eventUrl(sampleUser.getId().intValue(), sampleEvent.getId().intValue()))
                .header("Authorization", loginString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleEvent.getId().intValue()))
                .andExpect(jsonPath("$.title").value(sampleTitle))
                .andExpect(jsonPath("$.description").value(sampleDesc))
                .andExpect(jsonPath("$.canceled").value(sampleCanceled))
                .andExpect(jsonPath("$.visibility").value(sampleVisibility))
                .andExpect(jsonPath("$.startTime").value(startsWith(sampleStartTime)))
                .andExpect(jsonPath("$.endTime").value(startsWith(sampleEndTime)));
    }

    @Test
    public void shouldReturnWithHttpStatusCreatedAndALocationHeader() throws Exception
    {
        mockMvc.perform(post(eventUrl(1, null))
                .header("Authorization", loginString))
                .andExpect(header().string("Location", startsWith(eventUrl(1, null))))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldCreateEmptyEventWithDefaultValues() throws Exception
    {
        MvcResult response = mockMvc.perform(
                post(eventUrl(1, null))
                        .header("Authorization", loginString)
        ).andExpect(status().isCreated()).andReturn();

        String expectedStartTime = LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String expectedEndTime = LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0).plusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(get(location).header("Authorization", loginString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("New Event"))
                .andExpect(jsonPath("$.description").value(""))
                .andExpect(jsonPath("$.canceled").value(false))
                .andExpect(jsonPath("$.visibility").value("PRIVATE"))
                .andExpect(jsonPath("$.startTime").value(startsWith(expectedStartTime)))
                .andExpect(jsonPath("$.endTime").value(startsWith(expectedEndTime)))
                .andExpect(jsonPath("$.expired").value(false))
                .andExpect(jsonPath("$.happening").value(false));
    }

    @Test
    public void shouldCreateAnEventWithProvidedValue() throws Exception
    {
        MvcResult response = mockMvc.perform(post(eventUrl(1, null))
                .header("Authorization", loginString)
                .param("title", sampleTitle)
                .param("description", sampleDesc)
                .param("startTime", sampleStartTime)
                .param("endTime", sampleEndTime)
                .param("canceled", "true")
                .param("visibility", sampleVisibility)
        ).andExpect(status().isCreated()).andReturn();

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(get(location).header("Authorization", loginString))
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
        MvcResult response = mockMvc.perform(put(eventUrl(1, 123))
                .header("Authorization", loginString)
                .param("title", sampleTitle)
                .param("description", sampleDesc)
                .param("startTime", sampleStartTime)
                .param("endTime", sampleEndTime)
                .param("canceled", sampleCanceled ? "true" : "false")
                .param("visibility", sampleVisibility)
        ).andExpect(status().isNoContent()).andReturn();

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(get(location).header("Authorization", loginString))
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
    public void shouldCancelEvent() throws Exception
    {
        mockMvc.perform(delete(eventUrl(1, 123)).header("Authorization", loginString))
                .andExpect(status().isNoContent());

        boolean canceled = (Boolean) em.createQuery("SELECT e.canceled FROM Event e WHERE e.id = :event_id").setParameter("event_id", 123l).getSingleResult();

        assertTrue(canceled);
    }

    @Test
    public void shouldNotCancelEventAndReturnNotFound() throws Exception
    {
        mockMvc.perform(delete(eventUrl(1, 234)).header("Authorization", loginString))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateNewTicketSet() throws Exception
    {
        String query = "SELECT count(ts) FROM TicketSet ts WHERE ts.event.id = :event_id";

        long count = (long) em.createQuery(query).setParameter("event_id", 123l).getSingleResult();

        mockMvc.perform(post(ticketSetUrl(1, 123, null))
                .header("Authorization", loginString)
                .param("title", "Early Bird")
                .param("price", "25.00"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith(ticketSetUrl(1, 123, null))));

        assertEquals(count+1, (long) em.createQuery(query).setParameter("event_id", 123l).getSingleResult());
    }

    @Test
    public void shouldUpdateTicketSet() throws Exception
    {
        mockMvc.perform(put(ticketSetUrl(1, 123, 10))
                .header("Authorization", loginString)
                .param("title", "Updated title")
                .param("price", "30.00"))
                .andExpect(status().isNoContent());

        TicketSet ticketSet = (TicketSet) em.createQuery("SELECT ts FROM TicketSet ts WHERE ts.id = 10").getSingleResult();
        assertEquals(new TicketSet("Updated title", new BigDecimal(30)), ticketSet);
    }

    @Test
    public void shouldDeleteTicketSet() throws Exception
    {
        long count = (long) em.createQuery("SELECT count(ts) FROM TicketSet ts").getSingleResult();

        mockMvc.perform(delete(ticketSetUrl(1, 123, 10))
                .header("Authorization", loginString))
                .andExpect(status().isOk());

        assertEquals(count - 1, (long) em.createQuery("SELECT count(ts) FROM TicketSet ts").getSingleResult());
    }

    /*************************************************
     * Sad Path
     *************************************************/

    @Test
    public void shouldReturnHttpStatusNotFoundIfUpdateCannotFindResource() throws Exception
    {
        // Event with ID 345 doesn't exist
        assertNull(em.find(Event.class, 345l));

        // put operation should create a new one (with a different ID)
        mockMvc.perform(put(eventUrl(1, 345))
                .header("Authorization", loginString)
                .param("title", sampleTitle)
                .param("description", sampleDesc)
                .param("startTime", sampleStartTime)
                .param("endTime", sampleEndTime)
                .param("visibility", sampleVisibility))
                .andExpect(status().isNotFound());

    }

    @Test
    public void shouldReturnHttpNotFoundIfNoUserFound() throws Exception
    {
        // User with ID 234 doesn't exist
        assertNull(em.find(User.class, 234l));

        mockMvc.perform(put(eventUrl(234, 123)).header("Authorization", loginString))
                .andExpect(status().isNotFound());

        mockMvc.perform(get(eventUrl(234, 123)).header("Authorization", loginString))
                .andExpect(status().isNotFound());
    }


    @Test
    public void shouldNotUpdateEventOnInvalidRequest() throws Exception
    {
        Event beforePut = em.find(Event.class, 123l);

        mockMvc.perform(put(eventUrl(1, 123))
                .header("Authorization", loginString)
                .param("title", "Title")
                .param("description", "Desc")
                .param("visibility", "Invalid Visibility"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());

        Event afterPut = em.find(Event.class, 123l);

        assertTrue(afterPut.equals(beforePut));
    }

    @Test
    public void shouldReturnHttpStatusNotFoundForNonExistentEvent() throws Exception
    {
        mockMvc.perform(get(eventUrl(1, 555)).header("Authorization", loginString))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnHttpStatusBadRequestForNonNumericEventId() throws Exception
    {
        mockMvc.perform(get("/users/1/events/non_numeric").header("Authorization", loginString))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestForInvalidStatusAndVisibility() throws Exception
    {
        mockMvc.perform(post(eventUrl(1, null))
                .header("Authorization", loginString)
                .param("title", "Title")
                .param("description", "Desc")
                .param("visibility", "Invalid Visibility"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void shouldReturnBadRequestForMalformedTime() throws Exception
    {
        mockMvc.perform(post(eventUrl(1, null))
                .header("Authorization", loginString)
                .param("title", "Title")
                .param("description", "Desc")
                .param("startTime", "2015-03-12"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());

        mockMvc.perform(post(eventUrl(1, null))
                .header("Authorization", loginString)
                .param("title", "Title")
                .param("description", "Desc")
                .param("startTime", "Invalid time"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void shouldReturnBadRequestWhenEndTimeBeforeStartTime() throws Exception
    {
        mockMvc.perform(post(eventUrl(1, null))
                .header("Authorization", loginString)
                .param("title", "Title")
                .param("description", "Desc")
                .param("startTime", "2015-03-12T20:00:00")
                .param("endTime", "2015-03-12T19:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void shouldNotCreateOrUpdateOrDeleteTicketSetIfNoEventFound() throws Exception
    {
        // Event with ID 345 doesn't exist
        assertNull(em.find(Event.class, 345l));

        long count = (long) em.createQuery("SELECT count(ts) FROM TicketSet ts").getSingleResult();

        mockMvc.perform(post(ticketSetUrl(1, 234, null))
                .header("Authorization", loginString))
                .andExpect(status().isNotFound());

        assertEquals(count, (long) em.createQuery("SELECT count(ts) FROM TicketSet ts").getSingleResult());

        assertNull(em.find(TicketSet.class, 30l));

        mockMvc.perform(put(ticketSetUrl(1, 123, 30))
                .header("Authorization", loginString))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete(ticketSetUrl(1, 123, 30))
                .header("Authorization", loginString))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotCreateOrUpdateTicketSetAndReturnBadRequestIfInputIsInvalid() throws Exception
    {
        // todo consider moving validation test to another test

        mockMvc.perform(post(ticketSetUrl(1, 123, null))
                .header("Authorization", loginString)
                .param("title", "Sample title")
                .param("price", "-25.00"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(ticketSetUrl(1, 123, null))
                .header("Authorization", loginString)
                .param("title", "")
                .param("price", "25.00"))
                .andExpect(status().isBadRequest());

        // negative price
        mockMvc.perform(put(ticketSetUrl(1, 123, 10))
                .header("Authorization", loginString)
                .param("title", "Sample title")
                .param("price", "-25.00"))
                .andExpect(status().isBadRequest());

        // no title
        mockMvc.perform(put(ticketSetUrl(1, 123, 10))
                .header("Authorization", loginString)
                .param("price", "25.00"))
                .andExpect(status().isBadRequest());

        // no price
        mockMvc.perform(put(ticketSetUrl(1, 123, 10))
                .header("Authorization", loginString)
                .param("title", "A Title"))
                .andExpect(status().isBadRequest());
    }

    // todo should return forbidden for event that's does not belong to a user

    /*******************
     * Fixture
     *******************/

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
