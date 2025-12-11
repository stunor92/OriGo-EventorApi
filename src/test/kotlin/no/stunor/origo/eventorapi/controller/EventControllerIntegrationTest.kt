package no.stunor.origo.eventorapi.controller

import com.ninjasquad.springmockk.MockkBean
import no.stunor.origo.eventorapi.services.EventService
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Disabled("MockMvc not available in Spring Boot 4.0.0 - needs refactoring")
@SpringBootTest
@ActiveProfiles("test")
class EventControllerIntegrationTest {


    @MockkBean
    private lateinit var eventService: EventService

    @Test
    fun `getEvent should return event when valid eventorId and eventId provided`() {
        // TODO: Refactor to use RestClient or TestRestTemplate instead of MockMvc
        // Given
        /*
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
        */
    }

    @Test
    fun `getEventEntryList should return entry list when valid parameters provided`() {
        // TODO: Refactor to use RestClient or TestRestTemplate instead of MockMvc
        // Given
        /*
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
        */
    }
}
