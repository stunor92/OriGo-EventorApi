package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.api.exception.EventorNotFoundException
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.data.PersonRepository
import no.stunor.origo.eventorapi.model.calendar.CalendarRace
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.EventClassificationEnum
import no.stunor.origo.eventorapi.model.person.Person
import no.stunor.origo.eventorapi.services.converter.CalendarConverter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CalendarService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var eventorRepository: EventorRepository

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var eventorService: EventorService

    @Autowired
    private lateinit var calendarConverter: CalendarConverter

    fun getEventList(from: LocalDate, to: LocalDate, classifications: List<EventClassificationEnum>?, userId: String): List<CalendarRace> {
        val eventorList: List<Eventor?> = eventorRepository.findAll().collectList().block()?.toList() ?: listOf()

        val result: MutableList<CalendarRace> = ArrayList()

        for (eventor in eventorList) {
            if (eventor != null) {
                val persons: List<Person> = personRepository.findAllByUsersContainsAndEventorId(user = userId, eventorId = eventor.eventorId).collectList().block()?.toList()
                        ?: listOf()
                result.addAll(getEventList(eventor = eventor, from = from, to = to, organisations = null, classifications = classifications, persons = persons))
            }
        }
        return result
    }

    fun getEventList(eventorId: String, from: LocalDate, to: LocalDate, organisations: List<String>?, classifications: List<EventClassificationEnum>?, userId: String): List<CalendarRace> {
        val eventor = eventorRepository.findByEventorId(eventorId).block() ?: throw EventorNotFoundException()
        val persons: List<Person> = personRepository.findAllByUsersContainsAndEventorId(userId, eventor.eventorId).collectList().block()?.toList()
                ?: listOf()

        return getEventList(eventor = eventor, from = from, to = to, organisations = organisations, classifications = classifications, persons = persons )
    }

    private fun getEventList(eventor: Eventor, from: LocalDate, to: LocalDate, organisations: List<String>?, classifications: List<EventClassificationEnum>?, persons: List<Person>): List<CalendarRace> {
        val eventList = eventorService.getEventList(eventor, from, to, organisations, classifications)
        val events: MutableList<String?> = ArrayList()
        for (event in eventList!!.event) {
            events.add(event.eventId.content)
        }

        val personIds: MutableList<String?> = ArrayList()
        val organisationIds: MutableList<String?> = ArrayList()


        for (person in persons) {
            personIds.add(person.personId)
            organisationIds.addAll(person.memberships.keys)
        }

        log.info("Fetching competitor-count for persons {} and organisations {}.", personIds, organisationIds)
        val competitorCountList = eventorService.getCompetitorCounts(eventor, events, organisationIds, personIds)
        return calendarConverter.convertEvents(eventList, eventor, competitorCountList)
    }
}

