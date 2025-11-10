package no.stunor.origo.eventorapi.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.entry.EntryStatus
import no.stunor.origo.eventorapi.model.event.entry.PersonEntry
import no.stunor.origo.eventorapi.model.person.PersonName
import no.stunor.origo.eventorapi.services.EventService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

@WebMvcTest(EventController::class)
class EventControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var eventService: EventService

    @Test
    fun `getEvent should return event when valid eventorId and eventId provided`() {
        // Given
        val eventorId = "NOR"
        val eventId = "17535"
        val mockEvent = Event(
            id = UUID.randomUUID(),
            eventorId = eventorId,
            eventorRef = eventId,
            name = "Test Event",
            startDate = Timestamp.valueOf(LocalDateTime.now()),
            finishDate = Timestamp.valueOf(LocalDateTime.now().plusHours(3)),
            organisers = mutableListOf(),
            races = mutableListOf(),
            classes = mutableListOf(),
            documents = mutableListOf()
        )
        
        every { eventService.getEvent(eventorId, eventId) } returns mockEvent

        // When & Then
        mockMvc.perform(get("/event/$eventorId/$eventId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.eventorRef").value(eventId))
            .andExpect(jsonPath("$.name").value("Test Event"))
    }

    @Test
    fun `getEventEntryList should return entry list when valid parameters provided`() {
        // Given
        val eventorId = "NOR"
        val eventId = "17535"
        val mockEntries = listOf(
            PersonEntry(
                personId = "123",
                name = PersonName(given = "John", family = "Doe"),
                classId = "H21",
                raceId = "1",
                punchingUnits = mutableListOf(),
                status = EntryStatus.SignedUp
            ),
            PersonEntry(
                personId = "456",
                name = PersonName(given = "Jane", family = "Smith"),
                classId = "D21",
                raceId = "1",
                punchingUnits = mutableListOf(),
                status = EntryStatus.SignedUp
            )
        )
        
        every { eventService.getEntryList(eventorId, eventId) } returns mockEntries

        // When & Then
        mockMvc.perform(get("/event/$eventorId/$eventId/entry-list"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].personId").value("123"))
            .andExpect(jsonPath("$[1].personId").value("456"))
    }
}
