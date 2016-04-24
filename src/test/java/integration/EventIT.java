package integration;

import static org.hamcrest.CoreMatchers.*;

import app.data.Event;
import app.data.TicketSet;
import app.data.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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
    private Long sampleUserId;

    private Event sampleEvent;
    private Long sampleEventId;

    private String sampleTitle;
    private String sampleDesc;
    private String sampleVisibility;
    private String sampleStartTime;
    private String sampleEndTime;
    private Boolean sampleCanceled;

    private Long sampleTicketSetId;

    // Authentication token is put in Authorization header in each request
    private String authString;

    /**
     * @param userId
     * @param eventId
     * @param ticketSetId
     * @return TicketSet URL
     */
    private String ticketSetURL(Long userId, Long eventId, Long ticketSetId)
    {
        return eventURL(userId, eventId) + "/ticket-sets" + (ticketSetId == null ? "" : "/" + ticketSetId);
    }

    /**
     * @param userId
     * @param eventId
     * @return Event URL
     */
    private String eventURL(Long userId, Long eventId)
    {
        return "/api/users/" + userId + "/events" + (eventId != null ? "/" + eventId : "");
    }

    @Before
    public void login() throws Exception
    {
        authString = AuthenticationIT.getAuthTokenFor("user@example.com", "123456789", mockMvc);
    }

    @Before
    public void sampleInput() throws Exception
    {
        sampleEventId = 123l;
        sampleUserId = 1l;
        sampleTicketSetId = 10l;
        sampleEvent = em.find(Event.class, sampleEventId);
        sampleUser = sampleEvent.getUser();

        sampleTitle = sampleEvent.getTitle();
        sampleDesc = sampleEvent.getDescription();
        sampleVisibility = sampleEvent.getVisibility().toString();
        sampleStartTime = sampleEvent.getStartTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        sampleEndTime = sampleEvent.getEndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        sampleCanceled = sampleEvent.isCanceled();
    }

    private MockHttpServletRequestBuilder addAuthHeader(MockHttpServletRequestBuilder requestBuilder)
    {
        return requestBuilder.header("Authorization", authString);
    }

    /*************************************************
     * Happy Path
     *************************************************/

    @Test
    public void shouldReturnAnEvent() throws Exception
    {
        mockMvc.perform(addAuthHeader(get(eventURL(sampleUser.getId(), sampleEventId))))
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
        mockMvc.perform(addAuthHeader(post(eventURL(sampleUserId, null))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith(eventURL(sampleUserId, null))));
    }

    @Test
    public void shouldCreateEmptyEventWithDefaultValues() throws Exception
    {
        MvcResult response = mockMvc.perform(post(eventURL(sampleUserId, null))
                .header("Authorization", authString)).andExpect(status().isCreated()).andReturn();

        String expectedStartTime = ZonedDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String expectedEndTime = ZonedDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0).plusHours(sampleUserId).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(addAuthHeader(get(location)))
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
        MvcResult response = mockMvc.perform(post(eventURL(sampleUserId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm())
        ).andExpect(status().isCreated()).andReturn();

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(get(location).header("Authorization", authString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(sampleTitle))
                .andExpect(jsonPath("$.description").value(sampleDesc))
                .andExpect(jsonPath("$.startTime").value(startsWith(sampleStartTime)))
                .andExpect(jsonPath("$.endTime").value(startsWith(sampleEndTime)))
                .andExpect(jsonPath("$.canceled").value(sampleCanceled))
                .andExpect(jsonPath("$.visibility").value(sampleVisibility));
    }

    private String getEventForm() throws JsonProcessingException
    {
        return getEventForm(sampleTitle, sampleDesc, sampleStartTime, sampleEndTime, sampleVisibility, sampleCanceled);
    }

    private String getEventForm(String title, String desc, String startTime, String endTime, String visibility, boolean isCancel) throws JsonProcessingException
    {
        class EventForm
        {
            public String title;
            public String description;
            public String startTime;
            public String endTime;
            public String visibility;
            public boolean canceled;

            public EventForm(String title, String description, String startTime, String endTime, String visibility, boolean canceled)
            {
                this.title = title;
                this.description = description;
                this.startTime = startTime;
                this.endTime = endTime;
                this.visibility = visibility;
                this.canceled = canceled;
            }
        }
        return new ObjectMapper().writeValueAsString(new EventForm(title, desc, startTime, endTime, visibility, isCancel));
    }

    @Test
    public void shouldUpdateAnExistingEvent() throws Exception
    {
        MvcResult response = mockMvc.perform(put(eventURL(sampleUserId, sampleEventId))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm())
        ).andExpect(status().isNoContent()).andReturn();

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(get(location).header("Authorization", authString))
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
        mockMvc.perform(delete(eventURL(sampleUserId, sampleEventId)).header("Authorization", authString))
                .andExpect(status().isNoContent());

        boolean canceled = (Boolean) em.createQuery("SELECT e.canceled FROM Event e WHERE e.id = :event_id").setParameter("event_id", sampleEventId).getSingleResult();

        assertTrue(canceled);
    }

    @Test
    public void shouldNotCancelEventAndReturnNotFound() throws Exception
    {
        mockMvc.perform(delete(eventURL(sampleUserId, 234l)).header("Authorization", authString))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateNewTicketSet() throws Exception
    {
        String query = "SELECT count(ts) FROM TicketSet ts WHERE ts.event.id = :event_id";

        long count = (long) em.createQuery(query).setParameter("event_id", sampleEventId).getSingleResult();

        mockMvc.perform(post(ticketSetURL(sampleUserId, sampleEventId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getTicketSetForm("Early Bird", "20.00")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith(ticketSetURL(sampleUserId, sampleEventId, null))));

        assertEquals(count + 1, (long) em.createQuery(query).setParameter("event_id", sampleEventId).getSingleResult());
    }

    @Test
    public void shouldUpdateTicketSet() throws Exception
    {
        mockMvc.perform(put(ticketSetURL(sampleUserId, sampleEventId, sampleTicketSetId))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getTicketSetForm("Updated title", "30.00")))
                .andExpect(status().isNoContent());

        TicketSet ticketSet = (TicketSet) em.createQuery("SELECT ts FROM TicketSet ts WHERE ts.id = " + sampleTicketSetId).getSingleResult();
        assertEquals(new TicketSet("Updated title", new BigDecimal(30)), ticketSet);
    }

    private String getTicketSetForm(String title, String price) throws JsonProcessingException
    {
        class TicketSetForm
        {
            public String title;
            public String price;

            public TicketSetForm(String title, String price)
            {
                this.title = title;
                this.price = price;
            }
        }
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(new TicketSetForm(title, price));
    }

    @Test
    public void shouldDeleteTicketSet() throws Exception
    {
        long count = (long) em.createQuery("SELECT count(ts) FROM TicketSet ts").getSingleResult();

        mockMvc.perform(delete(ticketSetURL(sampleUserId, sampleEventId, sampleTicketSetId))
                .header("Authorization", authString))
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
        mockMvc.perform(put(eventURL(sampleUserId, 345l))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm()))
                .andExpect(status().isNotFound());

    }

    @Test
    public void shouldReturnHttpNotFoundIfNoUserFound() throws Exception
    {
        // User with ID 234 doesn't exist
        assertNull(em.find(User.class, 234l));

        mockMvc.perform(put(eventURL(234l, sampleEventId))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get(eventURL(234l, sampleEventId)).header("Authorization", authString))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotUpdateEventOnInvalidRequest() throws Exception
    {
        Event beforePut = em.find(Event.class, sampleEventId);

        mockMvc.perform(put(eventURL(sampleUserId, sampleEventId))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm(sampleTitle, sampleDesc, sampleStartTime, sampleEndTime, "Invalid Visibility", false)))
                .andExpect(status().isBadRequest());

        // the event should not be updated
        Event afterPut = em.find(Event.class, sampleEventId);
        assertTrue(afterPut.equals(beforePut));

        // should not create new event
        long countBefore = em.createQuery("SELECT COUNT(e) FROM Event e").getFirstResult();
        mockMvc.perform(post(eventURL(sampleUserId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm(sampleTitle, sampleDesc, sampleStartTime, sampleEndTime, "Invalid Visibility", false)))
                .andExpect(status().isBadRequest());
        long countAfter = em.createQuery("SELECT COUNT(e) FROM Event e").getFirstResult();
        assertEquals(countBefore, countAfter);
    }

    @Test
    public void shouldReturnHttpStatusNotFoundForNonExistentEvent() throws Exception
    {
        mockMvc.perform(get(eventURL(sampleUserId, 555l))
                .header("Authorization", authString))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnHttpStatusBadRequestForNonNumericEventId() throws Exception
    {
        mockMvc.perform(get(eventURL(1l, null) + "/nonnumeric")
                .header("Authorization", authString))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestForMalformedTime() throws Exception
    {
        mockMvc.perform(post(eventURL(sampleUserId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm(sampleTitle, sampleDesc, "2015-03-04", sampleEndTime, sampleVisibility, false)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());

        mockMvc.perform(post(eventURL(sampleUserId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm(sampleTitle, sampleDesc, "2015-40-04T00:00:00.000Z", sampleEndTime, sampleVisibility, false)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(eventURL(sampleUserId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm(sampleTitle, sampleDesc, "Invalid Time", sampleEndTime, sampleVisibility, false)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void shouldReturnBadRequestWhenEndTimeBeforeStartTime() throws Exception
    {
        String invalidStartTime = ZonedDateTime.of(2015, 3, 12, 20, 00, 00, 00, ZoneId.of("Z")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String invalidEndTime = ZonedDateTime.of(2015, 3, 12, 19, 00, 00, 00, ZoneId.of("Z")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        mockMvc.perform(post(eventURL(sampleUserId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm(sampleTitle, sampleDesc, invalidStartTime, invalidEndTime, sampleVisibility, false)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void shouldNotCreateOrUpdateOrDeleteTicketSetIfNoEventFound() throws Exception
    {
        assertNull(em.find(Event.class, 345l));

        long count = (long) em.createQuery("SELECT count(ts) FROM TicketSet ts").getSingleResult();

        mockMvc.perform(post(ticketSetURL(sampleUserId, 234l, null))
                .header("Authorization", authString))
                .andExpect(status().isNotFound());

        assertEquals(count, (long) em.createQuery("SELECT count(ts) FROM TicketSet ts").getSingleResult());

        // update
        assertNull(em.find(TicketSet.class, 30l));

        mockMvc.perform(put(ticketSetURL(sampleUserId, sampleEventId, 30l))
                .header("Authorization", authString))
                .andExpect(status().isNotFound());

        // delete
        mockMvc.perform(delete(ticketSetURL(sampleUserId, sampleEventId, 30l))
                .header("Authorization", authString))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotCreateOrUpdateTicketSetAndReturnBadRequestIfInputIsInvalid() throws Exception
    {
        mockMvc.perform(post(ticketSetURL(sampleUserId, sampleEventId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getTicketSetForm("Updated title", "-30.00")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(ticketSetURL(sampleUserId, sampleEventId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getTicketSetForm("", "30.00")))
                .andExpect(status().isBadRequest());

        // negative price
        mockMvc.perform(put(ticketSetURL(sampleUserId, sampleEventId, sampleTicketSetId))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getTicketSetForm("", "-30.00")))
                .andExpect(status().isBadRequest());

        // no title
        mockMvc.perform(put(ticketSetURL(sampleUserId, sampleEventId, sampleTicketSetId))
                .header("Authorization", authString)
                .contentType("application/json")
                .content("{\"price\": \"25.00\"}"))
                .andExpect(status().isBadRequest());

        // no price
        mockMvc.perform(put(ticketSetURL(sampleUserId, sampleEventId, sampleTicketSetId))
                .header("Authorization", authString)
                .contentType("application/json")
                .content("{\"title\": \"Title\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnForbiddenAccessWhileAccessingEventsOfOtherUser() throws Exception
    {
        Long anotherUserId = 2l;
        Long anotherEventId = 456l;
        Long anotherTicketSetId = 9l;

        mockMvc.perform(get(eventURL(anotherUserId, anotherEventId))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());

        mockMvc.perform(post(eventURL(anotherUserId, null))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());

        mockMvc.perform(put(eventURL(anotherUserId, anotherEventId))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete(eventURL(anotherUserId, anotherEventId))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());

        mockMvc.perform(post(ticketSetURL(anotherUserId, anotherEventId, null))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());

        mockMvc.perform(put(ticketSetURL(anotherUserId, anotherEventId, anotherTicketSetId))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete(ticketSetURL(anotherUserId, anotherEventId, anotherTicketSetId))
                .header("Authorization", authString))
                .andExpect(status().isForbidden());
    }

    /*******************
     * Fixture
     *******************/

    @Test
    public void shouldLoadTestFixture() throws Exception
    {
        assertNotNull(em.createQuery("SELECT e FROM Event e WHERE e.id = " + sampleEventId).getSingleResult());
    }

    @Override
    protected IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream("/fixtures/events_dataset.xml"));
    }
}
