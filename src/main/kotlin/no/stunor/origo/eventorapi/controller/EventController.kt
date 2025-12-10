package no.stunor.origo.eventorapi.controller

import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.entry.Entry
import no.stunor.origo.eventorapi.services.EventService
import no.stunor.origo.eventorapi.validation.InputValidator
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
    private lateinit var inputValidator: InputValidator

    @GetMapping("/{eventorId}/{eventId}")
    fun getEvent(
        @PathVariable eventorId: String,
        @PathVariable eventId: String
    ): ResponseEntity<Event> {
        // Validate inputs to prevent SSRF attacks
        val validatedEventorId = inputValidator.validateEventorId(eventorId)
        val validatedEventId = inputValidator.validateEventId(eventId)

        return ResponseEntity(
            eventService.getEvent(validatedEventorId, validatedEventId),
            HttpStatus.OK
        )
    }

    @GetMapping("/{eventorId}/{eventId}/entry-list")
    fun getEventEntryList(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String
    ): ResponseEntity<List<Entry>> {
        // Validate inputs to prevent SSRF attacks
        val validatedEventorId = inputValidator.validateEventorId(eventorId)
        val validatedEventId = inputValidator.validateEventId(eventId)

        return ResponseEntity(
                eventService.getEntryList(
                        eventorId = validatedEventorId,
                        eventId = validatedEventId
                ),
                HttpStatus.OK
        )
    }
}
