package no.stunor.origo.eventorapi.controller

import com.google.firebase.auth.FirebaseAuth
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
import java.time.LocalDate

@RestController
internal class EventorApiController {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var calendarService: CalendarService

    @Autowired
    private lateinit var competitorService: CompetitorService

    @Autowired
    private lateinit var eventService: EventService

    @GetMapping("/person/download")
    fun HttpServletRequest.authenticate(
        @RequestHeader(value = "eventorId") eventorId: String,
        @RequestHeader(value = "username") username: String,
        @RequestHeader(value = "password") password: String
    ): ResponseEntity<Person> {
        log.info("Start authenticating user {}.", username)
        val uid = getAttribute("uid") as String
        return ResponseEntity(
                authService.authenticate(
                        eventorId =eventorId,
                        username = username,
                        password = password,
                        userId = uid
                )
                , HttpStatus.OK
        )
    }

    @GetMapping("/eventlist/{eventorId}")
    fun HttpServletRequest.getEventList(
        @PathVariable("eventorId") eventorId: String,
        @RequestParam("from") from: LocalDate,
        @RequestParam("to") to: LocalDate,
        @RequestParam(value = "organisations", required = false) organisations: List<String>?,
        @RequestParam(value = "classifications", required = false) classifications: List<EventClassificationEnum>?
    ): ResponseEntity<List<CalendarRace>> {
        log.info("Start to get event-list from eventor-{}.", eventorId)
        val uid = getAttribute("uid") as String
        return ResponseEntity(
                calendarService.getEventList(
                        eventorId = eventorId,
                        from = from,
                        to = to,
                        organisations = organisations,
                        classifications = classifications,
                        userId = uid
                ),
                HttpStatus.OK
        )
    }

    @GetMapping("/eventlist")
    fun HttpServletRequest.getEventList(
        @RequestParam("from") from: LocalDate,
        @RequestParam("to") to: LocalDate,
        @RequestParam(value = "classifications", required = false) classifications: List<EventClassificationEnum>?
    ): ResponseEntity<List<CalendarRace>> {
        log.info("Start to get event-list from all eventors.")
        val uid = getAttribute("uid") as String
        return ResponseEntity(
                calendarService.getEventList(
                        from = from,
                        to = to,
                        classifications = classifications,
                        userId = uid
                ),
                HttpStatus.OK
        )
    }

    @GetMapping("/eventlist/me")
    fun HttpServletRequest.getUserEntries(): ResponseEntity<List<CalendarRace>> {
        val uid = getAttribute("uid") as String
        log.info("Start to get personal events for user {}.", uid)
        return ResponseEntity(
                calendarService.getEventList(
                        userId = uid
                ),
                HttpStatus.OK
        )
    }


    @GetMapping("/event/{eventorId}/{eventId}")
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

    @GetMapping("/event/{eventorId}/{eventId}/me")
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

    @GetMapping("/event/{eventorId}/{eventId}/entrylist")
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

    @GetMapping("/event/{eventorId}/{eventId}/startlist")
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

    @GetMapping("/event/{eventorId}/{eventId}/resultlist")
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

    @GetMapping("/event/{eventorId}/{eventId}/download")
    fun downloadEvent(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String
    ) {
        eventService.downloadEvent(eventorId, eventId)
    }

    @GetMapping("/event/{eventorId}/{eventId}/competitors/download")
    fun HttpServletRequest.downloadCompetitors(
        @PathVariable("eventorId") eventorId: String,
        @PathVariable("eventId") eventId: String
    ) {
        val uid = getAttribute("uid") as String
        eventService.downloadCompetitors(eventorId, eventId, uid)
    }
}
