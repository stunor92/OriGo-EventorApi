package no.stunor.origo.eventorapi.controller

import jakarta.servlet.http.HttpServletRequest
import no.stunor.origo.eventorapi.model.calendar.CalendarRace
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.EventClassificationEnum
import no.stunor.origo.eventorapi.model.event.competitor.Competitor
import no.stunor.origo.eventorapi.model.person.Person
import no.stunor.origo.eventorapi.services.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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


    @GetMapping("/person/{eventorId}/download")
    fun authenticate(@PathVariable(value = "eventorId") eventorId: String, @RequestHeader(value = "username") username: String, @RequestHeader(value = "password") password: String, request: HttpServletRequest): ResponseEntity<Person> {
        log.info("Start authenticating user {}.", username)
        val uid = request.getAttribute("uid") as String
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

    @GetMapping("/eventList/{eventorId}")
    fun getEventList(@PathVariable("eventorId") eventorId: String, @RequestParam("from") from: LocalDate, @RequestParam("to") to: LocalDate, @RequestParam(value = "organisations", required = false) organisations: List<String>?, @RequestParam(value = "classifications", required = false) classifications: List<EventClassificationEnum>?, request: HttpServletRequest): ResponseEntity<List<CalendarRace>> {
        log.info("Start to get event-list from eventor-{}.", eventorId)
        val uid = request.getAttribute("uid") as String
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

    @GetMapping("/eventList")
    fun getEventList(@RequestParam("from") from: LocalDate, @RequestParam("to") to: LocalDate, @RequestParam(value = "classifications", required = false) classifications: List<EventClassificationEnum>?, request: HttpServletRequest): ResponseEntity<List<CalendarRace>> {
        log.info("Start to get event-list from all eventors.")
        val uid = request.getAttribute("uid") as String
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

    @GetMapping("/eventList/user")
    fun getUserEntries(@RequestHeader("userId") userId: String): ResponseEntity<List<CalendarRace>> {
        log.info("Start to get personal events for user {}.", userId)
        return ResponseEntity(
                calendarService.getEventList(
                        userId = userId
                ),
                HttpStatus.OK
        )
    }


    @GetMapping("/event/{eventorId}/{eventId}")
    fun getEvent(@PathVariable("eventorId") eventorId: String, @PathVariable("eventId") eventId: String): ResponseEntity<Event> {
        return ResponseEntity(
                eventService.getEvent(
                        eventorId = eventorId,
                        eventId = eventId
                ),
                HttpStatus.OK
        )
    }

    @GetMapping("/event/{eventorId}/{eventId}/me")
    fun getEvent(@PathVariable("eventorId") eventorId: String, @PathVariable("eventId") eventId: String, request: HttpServletRequest): ResponseEntity<List<Competitor>> {
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

    @GetMapping("/event/{eventorId}/{eventId}/entrylist")
    fun getEventEntryList(@PathVariable("eventorId") eventorId: String, @PathVariable("eventId") eventId: String): ResponseEntity<List<Competitor>> {
        return ResponseEntity(
                eventService.getEntryList(
                        eventorId = eventorId,
                        eventId = eventId
                ),
                HttpStatus.OK
        )
    }

    @GetMapping("/event/{eventorId}/{eventId}/startlist")
    fun getEventStartList(@PathVariable("eventorId") eventorId: String, @PathVariable("eventId") eventId: String): ResponseEntity<List<Competitor>> {
        return ResponseEntity(
                eventService.getStartList(
                        eventorId = eventorId,
                        eventId = eventId
                ),
                HttpStatus.OK
        )
    }

    @GetMapping("/event/{eventorId}/{eventId}/resultlist")
    fun getEventResultList(@PathVariable("eventorId") eventorId: String, @PathVariable("eventId") eventId: String): ResponseEntity<List<Competitor>> {
        return ResponseEntity(
                eventService.getResultList(
                        eventorId = eventorId,
                        eventId = eventId
                ),
                HttpStatus.OK
        )
    }

    @GetMapping("/event/{eventorId}/{eventId}/download")
    fun downloadEvent(@PathVariable("eventorId") eventorId: String, @PathVariable("eventId") eventId: String) {
        eventService.downloadEvent(eventorId, eventId)
    }

    @GetMapping("/event/{eventorId}/{eventId}/competitors/download")
    fun downloadCompetitors(@PathVariable("eventorId") eventorId: String, @PathVariable("eventId") eventId: String, @RequestHeader(value = "userId") userId: String) {
        eventService.downloadCompetitors(eventorId, eventId, userId)
    }
}
