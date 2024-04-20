package no.stunor.origo.eventorapi.services

import lombok.extern.slf4j.Slf4j
import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.controller.exception.InvalidInputException
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.services.converter.EventConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Slf4j
@Service
class OrganiserService {
    @Autowired
    private lateinit var eventorRepository: EventorRepository

    @Autowired
    private lateinit var eventorService: EventorService

    @Autowired
    private lateinit var eventConverter: EventConverter


    fun listEvents(eventorId: String?, organisationId: String): List<Event> {
        val eventor = eventorRepository.findByEventorId(eventorId).block()
                ?: throw InvalidInputException("Eventor not found")

        val fromDate = LocalDate.now().minusMonths(1)
        val toDate = LocalDate.now().plusMonths(1)

        return eventConverter.convertEvents(eventList = eventorService.getEventList(eventor = eventor, fromDate = fromDate, toDate = toDate, organisationIds = listOf(organisationId), classifications = null), eventor = eventor)
    }
}