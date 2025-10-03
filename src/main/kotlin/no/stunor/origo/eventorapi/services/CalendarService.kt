package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.exception.EventorNotFoundException
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.data.PersonRepository
import no.stunor.origo.eventorapi.model.calendar.CalendarRace
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.EventClassificationEnum
import no.stunor.origo.eventorapi.model.person.Person
import no.stunor.origo.eventorapi.services.converter.CalendarConverter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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

    @Value("\${config.personalEntries.start}")
    private val personalEntriesStart = 0L

    @Value("\${config.personalEntries.end}")
    private val personalEntriesEnd = 0L

    @Value("\${config.personalStarts.start}")
    private val personalStartsStart = 0L

    @Value("\${config.personalStarts.end}")
    private val personalStartsEnd = 0L

    @Value("\${config.personalResults.start}")
    private val personalResultsStart = 0L

    @Value("\${config.personalResults.end}")
    private val personalResultsEnd = 0L

    fun getEventList(userId: String): List<CalendarRace> {
        val raceList = mutableListOf<CalendarRace>()
        val persons = personRepository.findAllByUsers(userId)

        for (person in persons) {
            val eventor = eventorRepository.findByEventorId(person.eventorId) ?: continue
            val organisationIds = person.memberships.map { it.organisationId }
            val entryList = eventorService.getGetOrganisationEntries(
                eventor = eventor,
                organisations = organisationIds,
                eventId = null,
                fromDate = LocalDate.now().minusDays(personalEntriesStart),
                toDate = LocalDate.now().plusDays(personalEntriesEnd)
            )
            val eventClassMap = buildEventClassMap(entryList, eventor)
            val startListList = eventorService.getGetPersonalStarts(
                eventor = eventor,
                personId = person.personId,
                eventId = null,
                fromDate = LocalDate.now().minusDays(personalStartsStart),
                toDate = LocalDate.now().plusDays(personalStartsEnd)
            )
            val resultListList = eventorService.getGetPersonalResults(
                eventor = eventor,
                personId = person.personId,
                eventId = null,
                fromDate = LocalDate.now().minusDays(personalResultsStart),
                toDate = LocalDate.now().plusDays(personalResultsEnd)
            )
            val personRaces = eventClassMap.generateCalendarRaceForPerson(
                eventor,
                person,
                entryList,
                startListList,
                resultListList
            )
            mergeRaces(raceList, personRaces)
        }
        return raceList
    }

    private fun buildEventClassMap(
        entryList: org.iof.eventor.EntryList,
        eventor: Eventor
    ): MutableMap<String, org.iof.eventor.EventClassList> {
        val eventClassMap = mutableMapOf<String, org.iof.eventor.EventClassList>()
        for (entry in entryList.entry) {
            for (raceId in entry.eventRaceId) {
                if (!eventClassMap.containsKey(raceId.content)) {
                    val eventClassList = eventorService.getEventClasses(eventor, entry.event.eventId.content)
                    if (eventClassList != null) {
                        eventClassMap[raceId.content] = eventClassList
                    }
                }
            }
        }
        return eventClassMap
    }

    private fun mergeRaces(
        raceList: MutableList<CalendarRace>,
        personRaces: List<CalendarRace>
    ) {
        for (race in personRaces) {
            val existingRace = raceList.find { it.eventor == race.eventor && it.raceId == race.raceId }
            if (existingRace != null) {
                existingRace.userEntries.addAll(race.userEntries)
                existingRace.organisationEntries.addAll(race.organisationEntries)
            } else {
                raceList.add(race)
            }
        }
    }

    fun getEventList(from: LocalDate, to: LocalDate, classifications: List<EventClassificationEnum>?, userId: String): List<CalendarRace> {
        val eventorList: MutableIterable<Eventor> = eventorRepository.findAll()

        val result: MutableList<CalendarRace> = ArrayList()

        for (eventor in eventorList) {
            val persons: List<Person> = personRepository.findAllByUsersAndEventorId(userId = userId, eventorId = eventor.eventorId)
            result.addAll(getEventList(eventor = eventor, from = from, to = to, organisations = null, classifications = classifications, persons = persons))

        }
        return result
    }

    fun getEventList(eventorId: String, from: LocalDate, to: LocalDate, organisations: List<String>?, classifications: List<EventClassificationEnum>?, userId: String): List<CalendarRace> {
        val eventor = eventorRepository.findByEventorId(eventorId) ?: throw EventorNotFoundException()
        val persons: List<Person> = personRepository.findAllByUsersAndEventorId(userId = userId, eventorId = eventor.eventorId)
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
            organisationIds.addAll(person.memberships.map { it.organisationId })
        }

        log.info("Fetching competitor-count for persons {} and organisations {}.", personIds, organisationIds)
        val competitorCountList = eventorService.getCompetitorCounts(eventor, events, organisationIds, personIds)
        return calendarConverter.convertEvents(eventList, eventor, competitorCountList)
    }

    private fun MutableMap<String, org.iof.eventor.EventClassList>.generateCalendarRaceForPerson(
        eventor: Eventor,
        person: Person,
        entryList: org.iof.eventor.EntryList?,
        startListList: org.iof.eventor.StartListList?,
        resultListList: org.iof.eventor.ResultListList?
    ): List<CalendarRace> {
        var result = calendarConverter.convertEntryList(eventor, entryList, person, this)
        result = calendarConverter.convertStartListList(eventor, startListList, person, result)
        result = calendarConverter.convertResultList(eventor, resultListList, person, result)
        return result.values.stream().toList()
    }
}

