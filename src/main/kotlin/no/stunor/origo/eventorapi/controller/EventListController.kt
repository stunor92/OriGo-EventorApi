package no.stunor.origo.eventorapi.controller

import jakarta.servlet.http.HttpServletRequest
import no.stunor.origo.eventorapi.model.calendar.CalendarRace
import no.stunor.origo.eventorapi.model.event.EventClassificationEnum
import no.stunor.origo.eventorapi.services.CalendarService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("event-list")
internal class EventListController {
    private val log = LoggerFactory.getLogger(this.javaClass)
    @Autowired
    private lateinit var calendarService: CalendarService


    @GetMapping("/{eventorId}")
    fun getEventList(
        @PathVariable("eventorId") eventorId: String,
        @RequestParam("from") from: LocalDate,
        @RequestParam("to") to: LocalDate,
        @RequestParam(
            value = "organisations",
            required = false
        ) organisations: List<String>?,
        @RequestParam(
            value = "classifications",
            required = false,
            defaultValue = "Championship, National, Regional, Local"
        ) classifications: List<EventClassificationEnum>?,
        request: HttpServletRequest
    ): ResponseEntity<List<CalendarRace>> {
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

    @GetMapping
    fun getEventList(
        @RequestParam("from") from: LocalDate,
        @RequestParam("to") to: LocalDate,
        @RequestParam(
            value = "classifications",
            required = false,
            defaultValue = "Championship, National, Regional, Local"
        ) classifications: List<EventClassificationEnum>?,
        request: HttpServletRequest
    ): ResponseEntity<List<CalendarRace>> {
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

    @GetMapping("/me")
    fun getUserEntries(
        request: HttpServletRequest
    ): ResponseEntity<List<CalendarRace>> {
        val uid = request.getAttribute("uid") as String
        log.info("Start to get personal events for user {}.", uid)
        return ResponseEntity(
                calendarService.getEventList(
                        userId = uid
                ),
                HttpStatus.OK
        )
    }
}
