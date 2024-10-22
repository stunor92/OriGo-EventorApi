package no.stunor.origo.eventorapi.controller

import jakarta.servlet.http.HttpServletRequest
import no.stunor.origo.eventorapi.model.calendar.CalendarRace
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.EventClassificationEnum
import no.stunor.origo.eventorapi.model.event.competitor.Competitor
import no.stunor.origo.eventorapi.model.person.Person
import no.stunor.origo.eventorapi.services.AuthService
import no.stunor.origo.eventorapi.services.CalendarService
import no.stunor.origo.eventorapi.services.CompetitorService
import no.stunor.origo.eventorapi.services.EventService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDate

@RestController
@RequestMapping("event")
internal class EventController {
    @Autowired
    private lateinit var eventService: EventService

    @Autowired
    private lateinit var competitorService: CompetitorService

    @GetMapping("/{eventorId}/{eventId}")
    fun getEvent(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String
    ): ResponseEntity<Event> {
        return ResponseEntity(
                eventService.getEvent(
                        eventorId = eventorId,
                        eventId = eventId
                ),
                HttpStatus.OK
        )
    }

    @GetMapping("/{eventorId}/{eventId}/me")
    fun HttpServletRequest.getEvent(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String
    ): ResponseEntity<List<Competitor>> {
        val uid = getAttribute("uid") as String
        return ResponseEntity(
                competitorService.getCompetitors(
                        eventorId = eventorId,
                        eventId = eventId,
                        userId = uid
                ),
                HttpStatus.OK
        )
    }

    @GetMapping("/{eventorId}/{eventId}/entrylist")
    fun getEventEntryList(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String
    ): ResponseEntity<List<Competitor>> {
        return ResponseEntity(
                eventService.getEntryList(
                        eventorId = eventorId,
                        eventId = eventId
                ),
                HttpStatus.OK
        )
    }

    @GetMapping("/{eventorId}/{eventId}/startlist")
    fun getEventStartList(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String
    ): ResponseEntity<List<Competitor>> {
        return ResponseEntity(
                eventService.getStartList(
                        eventorId = eventorId,
                        eventId = eventId
                ),
                HttpStatus.OK
        )
    }

    @GetMapping("/{eventorId}/{eventId}/resultlist")
    fun getEventResultList(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String
    ): ResponseEntity<List<Competitor>> {
        return ResponseEntity(
                eventService.getResultList(
                        eventorId = eventorId,
                        eventId = eventId
                ),
                HttpStatus.OK
        )
    }

    @GetMapping("/{eventorId}/{eventId}/download")
    fun downloadEvent(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String
    ) {
        eventService.downloadEvent(eventorId, eventId)
        eventService.downloadEventClasses(eventorId, eventId)
    }

    @GetMapping("/{eventorId}/{eventId}/competitors/download")
    fun HttpServletRequest.downloadCompetitors(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String
    ) {
        val uid = getAttribute("uid") as String
        eventService.downloadCompetitors(eventorId, eventId, uid)
    }
}
