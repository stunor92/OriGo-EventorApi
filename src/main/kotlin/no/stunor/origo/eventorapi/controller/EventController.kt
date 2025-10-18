package no.stunor.origo.eventorapi.controller

import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.entry.Entry
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

    @GetMapping("/{eventorId}/{eventId}")
    fun getEvent(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String
    ): ResponseEntity<Event> {
        return ResponseEntity(
            eventService.getEvent(eventorId, eventId),
            HttpStatus.OK
        )
    }

    @GetMapping("/{eventorId}/{eventId}/entry-list")
    fun getEventEntryList(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String
    ): ResponseEntity<List<Entry>> {
        return ResponseEntity(
                eventService.getEntryList(
                        eventorId = eventorId,
                        eventId = eventId
                ),
                HttpStatus.OK
        )
    }
}
