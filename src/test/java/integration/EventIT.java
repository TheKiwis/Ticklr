package integration;

import static org.hamcrest.CoreMatchers.*;

import app.data.event.Event;
import app.data.event.TicketSet;
import app.data.user.User;
import app.web.event.EventURI;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author ngnmhieu
 */
public class EventIT extends CommonTestTest
{
    // loaded fixtures from database
    private User sampleUser;
    private UUID sampleUserId;

    private Event sampleEvent;
    private Long sampleEventId;

    private String sampleTitle;
    private String sampleDesc;
    private String sampleStartTime;
    private String sampleEndTime;
    private Boolean sampleCanceled;
    private Boolean samplePublic;

    private Long sampleTicketSetId;

    // Authentication token is put in Authorization header in each request
    private String authString;

    private UUID invalidUserId;

    private EventURI eventURI;

    /**
     * @param userId
     * @param eventId
     * @param ticketSetId
     * @return TicketSet URL
     */
    private String ticketSetURL(UUID userId, Long eventId, Long ticketSetId)
    {
        return eventURI.ticketSetURL(userId, eventId, ticketSetId);
    }

    /**
     * @param userId
     * @param eventId
     * @return Event URL
     */
    private String eventURL(UUID userId, Long eventId)
    {
        return eventURI.eventURL(userId, eventId);
    }

    @Before
    public void setup()
    {
        eventURI = new EventURI(hostname);
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
        sampleUserId = UUID.fromString("4eab8080-0f0e-11e6-9f74-0002a5d5c51b");
        sampleTicketSetId = 10l;
        sampleEvent = em.find(Event.class, sampleEventId);
        sampleUser = sampleEvent.getUser();

        sampleTitle = sampleEvent.getTitle();
        sampleDesc = sampleEvent.getDescription();
        sampleStartTime = sampleEvent.getStartTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        sampleEndTime = sampleEvent.getEndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        sampleCanceled = sampleEvent.isCanceled();
        samplePublic = sampleEvent.isPublic();

        invalidUserId = UUID.fromString("f3958500-0f0e-11e6-b1d4-0002a5d5c51b");
    }

    private MockHttpServletRequestBuilder prepareRequest(MockHttpServletRequestBuilder requestBuilder)
    {
        return requestBuilder
                .header("Authorization", authString)
                .contentType("application/json");
    }

    /*************************************************
     * Happy Path
     *************************************************/

    @Test
    public void happy_should_return_events_collection() throws Exception
    {
        String url = eventURL(sampleUserId, null);
        mockMvc.perform(prepareRequest(get(url)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.href").value(url))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.items[0].id").isNotEmpty())
                .andExpect(jsonPath("$.items[0].href").isNotEmpty());
    }

    @Test
    public void happy_should_return_an_event() throws Exception
    {
        mockMvc.perform(prepareRequest(get(eventURL(sampleUser.getId(), sampleEventId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleEvent.getId().intValue()))
                .andExpect(jsonPath("$.href").isNotEmpty())
                .andExpect(jsonPath("$.title").value(sampleTitle))
                .andExpect(jsonPath("$.description").value(sampleDesc))
                .andExpect(jsonPath("$.canceled").value(sampleCanceled))
                .andExpect(jsonPath("$.isPublic").value(samplePublic))
                .andExpect(jsonPath("$.expired").isNotEmpty())
                .andExpect(jsonPath("$.happening").isNotEmpty())
                .andExpect(jsonPath("$.ticketSets").isMap())
                .andExpect(jsonPath("$.startTime").value(startsWith(sampleStartTime)))
                .andExpect(jsonPath("$.endTime").value(startsWith(sampleEndTime)));

        // expanding ticketSets
        mockMvc.perform(prepareRequest(get(eventURL(sampleUser.getId(), sampleEventId)))
                .param("expand", "ticketSets.items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketSets.items").isArray())
                .andExpect(jsonPath("$.ticketSets.items[0].title").isNotEmpty());
    }

    @Test
    public void happy_should_create_empty_event_with_default_values() throws Exception
    {
        MvcResult response = mockMvc.perform(prepareRequest(post(eventURL(sampleUserId, null))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith(eventURL(sampleUserId, null))))
                .andReturn();

        String expectedStartTime = ZonedDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String expectedEndTime = ZonedDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0).plusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(prepareRequest(get(location)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("New Event"))
                .andExpect(jsonPath("$.description").value(""))
                .andExpect(jsonPath("$.canceled").value(false))
                .andExpect(jsonPath("$.isPublic").value(false))
                .andExpect(jsonPath("$.startTime").value(startsWith(expectedStartTime)))
                .andExpect(jsonPath("$.endTime").value(startsWith(expectedEndTime)))
                .andExpect(jsonPath("$.expired").value(false))
                .andExpect(jsonPath("$.happening").value(false));
    }

    @Test
    public void happy_should_create_an_event_with_provided_value() throws Exception
    {
        MvcResult response = mockMvc.perform(prepareRequest(post(eventURL(sampleUserId, null)))
                .content(getEventForm())
        ).andExpect(status().isCreated()).andReturn();

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(prepareRequest(get(location)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.href").value(location))
                .andExpect(jsonPath("$.title").value(sampleTitle))
                .andExpect(jsonPath("$.description").value(sampleDesc))
                .andExpect(jsonPath("$.startTime").value(startsWith(sampleStartTime)))
                .andExpect(jsonPath("$.endTime").value(startsWith(sampleEndTime)))
                .andExpect(jsonPath("$.canceled").value(sampleCanceled))
                .andExpect(jsonPath("$.isPublic").value(samplePublic));
    }

    private String getEventForm() throws JsonProcessingException
    {
        return getEventForm(sampleTitle, sampleDesc, sampleStartTime, sampleEndTime, sampleCanceled, samplePublic);
    }

    private String getEventForm(String title, String desc, String startTime, String endTime, boolean isCancel, boolean isPublic) throws JsonProcessingException
    {
        class EventForm
        {
            public String title;
            public String description;
            public String startTime;
            public String endTime;
            public boolean canceled;
            public boolean isPublic;

            public EventForm(String title, String description, String startTime, String endTime, boolean canceled, boolean isPublic)
            {
                this.title = title;
                this.description = description;
                this.startTime = startTime;
                this.endTime = endTime;
                this.canceled = canceled;
                this.isPublic = isPublic;
            }
        }
        return new ObjectMapper().writeValueAsString(new EventForm(title, desc, startTime, endTime, isCancel, isPublic));
    }

    @Test
    public void happy_should_update_an_existing_event() throws Exception
    {
        MvcResult response = mockMvc.perform(prepareRequest(put(eventURL(sampleUserId, sampleEventId))
                .content(getEventForm()))
        ).andExpect(status().isNoContent()).andReturn();

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(prepareRequest(get(location)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(sampleTitle))
                .andExpect(jsonPath("$.description").value(sampleDesc))
                .andExpect(jsonPath("$.startTime").value(startsWith(sampleStartTime)))
                .andExpect(jsonPath("$.endTime").value(startsWith(sampleEndTime)))
                .andExpect(jsonPath("$.canceled").value(sampleCanceled))
                .andExpect(jsonPath("$.isPublic").value(samplePublic));
    }

    @Test
    public void happy_should_cancel_event() throws Exception
    {
        mockMvc.perform(prepareRequest(delete(eventURL(sampleUserId, sampleEventId))))
                .andExpect(status().isNoContent());

        boolean canceled = (Boolean) em.createQuery("SELECT e.canceled FROM Event e WHERE e.id = :event_id").setParameter("event_id", sampleEventId).getSingleResult();

        assertTrue(canceled);
    }

    @Test
    public void happy_should_not_cancel_event_and_return_HTTP_NotFound() throws Exception
    {
        mockMvc.perform(prepareRequest(delete(eventURL(sampleUserId, 234l))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void happy_should_return_ticket_sets() throws Exception
    {
        mockMvc.perform(prepareRequest(get(ticketSetURL(sampleUserId, sampleEvent.getId(), null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.href").isNotEmpty())
                .andExpect(jsonPath("$.items").isArray());

        // expand
        mockMvc.perform(prepareRequest(get(ticketSetURL(sampleUserId, sampleEvent.getId(), null)))
                .param("expand", "items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].title").isNotEmpty())
                .andExpect(jsonPath("$.items[0].event").isEmpty());
    }

    @Test
    public void happy_should_return_a_ticket_set() throws Exception
    {
        mockMvc.perform(prepareRequest(get(ticketSetURL(sampleUserId, sampleEvent.getId(), sampleTicketSetId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.href").isNotEmpty())
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andExpect(jsonPath("$.event").isMap())
                .andExpect(jsonPath("$.event.title").doesNotExist());

        // expand
        mockMvc.perform(prepareRequest(get(ticketSetURL(sampleUserId, sampleEvent.getId(), sampleTicketSetId)))
                .param("expand", "event"))
                .andExpect(jsonPath("$.event.title").isNotEmpty());
    }

    @Test
    public void happy_should_create_new_ticket_set() throws Exception
    {
        String query = "SELECT count(ts) FROM TicketSet ts WHERE ts.event.id = :event_id";

        long count = (long) em.createQuery(query).setParameter("event_id", sampleEventId).getSingleResult();

        mockMvc.perform(prepareRequest(post(ticketSetURL(sampleUserId, sampleEventId, null)))
                .content(getTicketSetForm("Early Bird", 20.00, 20)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith(ticketSetURL(sampleUserId, sampleEventId, null))));

        assertEquals(count + 1, (long) em.createQuery(query).setParameter("event_id", sampleEventId).getSingleResult());
    }

    @Test
    public void happy_should_update_ticket_set() throws Exception
    {
        mockMvc.perform(put(ticketSetURL(sampleUserId, sampleEventId, sampleTicketSetId))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getTicketSetForm("Updated title", 30.00, 10)))
                .andExpect(status().isNoContent());

        TicketSet ticketSet = (TicketSet) em.createQuery("SELECT ts FROM TicketSet ts WHERE ts.id = " + sampleTicketSetId).getSingleResult();
        assertEquals("Updated title", ticketSet.getTitle());
        assertEquals(new BigDecimal(30), ticketSet.getPrice());
        assertEquals(10, ticketSet.getStock());
    }

    private String getTicketSetForm(String title, double price, int stock) throws JsonProcessingException
    {
        class TicketSetForm
        {
            public String title;
            public double price;
            public int stock;

            public TicketSetForm(String title, double price, int stock)
            {
                this.title = title;
                this.price = price;
                this.stock = stock;
            }
        }
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(new TicketSetForm(title, price, stock));
    }

    @Test
    public void happy_should_delete_ticket_set() throws Exception
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
    public void sad_should_return_HTTP_NotFound_when_update_event() throws Exception
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
    public void sad_should_return_HTTP_NotFound_if_no_user_found() throws Exception
    {
        // User with ID 234 doesn't exist
        assertNull(em.find(User.class, invalidUserId));

        mockMvc.perform(put(eventURL(invalidUserId, sampleEventId))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get(eventURL(invalidUserId, sampleEventId)).header("Authorization", authString))
                .andExpect(status().isNotFound());
    }

    @Test
    public void sad_should_not_update_event_on_invalid_request() throws Exception
    {
        // the event should not be updated
        Event beforePut = em.find(Event.class, sampleEventId);
        mockMvc.perform(put(eventURL(sampleUserId, sampleEventId))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm(sampleTitle, sampleDesc, sampleStartTime, "Invalid Time", false, true)))
                .andExpect(status().isBadRequest());
        Event afterPut = em.find(Event.class, sampleEventId);
        assertTrue(afterPut.equals(beforePut));


        // the event should not be updated
        beforePut = em.find(Event.class, sampleEventId);
        mockMvc.perform(put(eventURL(sampleUserId, sampleEventId))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm("", sampleDesc, sampleStartTime, sampleEndTime, false, true)))
                .andExpect(status().isBadRequest());
        afterPut = em.find(Event.class, sampleEventId);
        assertTrue(afterPut.equals(beforePut));


        // should not create new event
        long countBefore = em.createQuery("SELECT COUNT(e) FROM Event e").getFirstResult();
        mockMvc.perform(post(eventURL(sampleUserId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm("", sampleDesc, sampleStartTime, sampleEndTime, false, true)))
                .andExpect(status().isBadRequest());
        long countAfter = em.createQuery("SELECT COUNT(e) FROM Event e").getFirstResult();
        assertEquals(countBefore, countAfter);
    }

    @Test
    public void sad_should_return_HTTP_NotFound_for_non_existent_event() throws Exception
    {
        mockMvc.perform(get(eventURL(sampleUserId, 555l))
                .header("Authorization", authString))
                .andExpect(status().isNotFound());
    }

    @Test
    public void sad_should_return_HTTP_BadRequest_for_non_numeric_eventId() throws Exception
    {
        mockMvc.perform(get(eventURL(sampleUserId, null) + "/nonnumeric")
                .header("Authorization", authString))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(eventURL(sampleUserId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm(sampleTitle, sampleDesc, "2015-03-04", sampleEndTime, false, true)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());

        mockMvc.perform(post(eventURL(sampleUserId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm(sampleTitle, sampleDesc, "2015-40-04T00:00:00.000Z", sampleEndTime, false, true)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(eventURL(sampleUserId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm(sampleTitle, sampleDesc, "Invalid Time", sampleEndTime, false, true)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void sad_should_return_HTTP_BadRequest_when_endTime_before_startTime() throws Exception
    {
        String invalidStartTime = ZonedDateTime.of(2015, 3, 12, 20, 00, 00, 00, ZoneId.of("Z")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String invalidEndTime = ZonedDateTime.of(2015, 3, 12, 19, 00, 00, 00, ZoneId.of("Z")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        mockMvc.perform(post(eventURL(sampleUserId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getEventForm(sampleTitle, sampleDesc, invalidStartTime, invalidEndTime, false, true)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void sad_should_return_HTTP_NotFound_on_ticket_sets() throws Exception
    {
        mockMvc.perform(prepareRequest(get(ticketSetURL(sampleUserId, 456l, null))))
                .andExpect(status().isNotFound());

        mockMvc.perform(prepareRequest(get(ticketSetURL(UUID.randomUUID(), 123l, null))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void sad_should_return_HTTP_Forbidden_on_ticket_sets() throws Exception
    {
        mockMvc.perform(prepareRequest(get(ticketSetURL(UUID.fromString("63b8e800-0f0e-11e6-bec3-0002a5d5c51b"), 456l, null))))
                .andExpect(status().isForbidden());
    }


    @Test
    public void sad_should_not_create_or_update_or_delete_ticket_set_if_no_event_found() throws Exception
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
    public void sad_should_not_create_or_update_ticket_set_and_return_HTTP_BadRequest_on_invalid_input() throws Exception
    {
        mockMvc.perform(post(ticketSetURL(sampleUserId, sampleEventId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getTicketSetForm("Updated title", -30.00, 20)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(ticketSetURL(sampleUserId, sampleEventId, null))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getTicketSetForm("New Event", 30.00, -1)))
                .andExpect(status().isBadRequest());

        // negative price
        mockMvc.perform(put(ticketSetURL(sampleUserId, sampleEventId, sampleTicketSetId))
                .header("Authorization", authString)
                .contentType("application/json")
                .content(getTicketSetForm("", -30.00, 20)))
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
    public void sad_should_return_HTTP_ForbiddenAccess_while_accessing_events_of_other_user() throws Exception
    {
        UUID anotherUserId = UUID.fromString("63b8e800-0f0e-11e6-bec3-0002a5d5c51b");
        Long anotherEventId = 456l;
        Long anotherTicketSetId = 9l;

        mockMvc.perform(get(eventURL(anotherUserId, null)))
                .andExpect(status().isForbidden());

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
