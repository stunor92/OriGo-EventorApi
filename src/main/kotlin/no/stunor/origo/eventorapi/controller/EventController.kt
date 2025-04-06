package no.stunor.origo.eventorapi.controller

import jakarta.servlet.http.HttpServletRequest
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.competitor.Competitor
import no.stunor.origo.eventorapi.services.CompetitorService
import no.stunor.origo.eventorapi.services.EventService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    fun getEvent(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String,
        request: HttpServletRequest
    ): ResponseEntity<List<Competitor>> {
        val uid = request.getAttribute("uid") as String
        return ResponseEntity(
                competitorService.getCompetitors(
                        eventorId = eventorId,
                        eventId = eventId,
                        userId = uid
                ),
                HttpStatus.OK
        )
    }

    @GetMapping("/{eventorId}/{eventId}/entry-list")
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

    @GetMapping("/{eventorId}/{eventId}/start-list")
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

    @GetMapping("/{eventorId}/{eventId}/result-list")
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
    }

    @GetMapping("/{eventorId}/{eventId}/fees/download")
    fun downloadEntryFees(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String
    ) {
        eventService.downloadEntryFees(eventorId, eventId)
    }

    @GetMapping("/{eventorId}/{eventId}/competitors/download")
    fun HttpServletRequest.downloadCompetitors(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String,
        request: HttpServletRequest
    ) {
        val uid = request.getAttribute("uid") as String
        eventService.downloadCompetitors(eventorId, eventId, uid)
    }
}
