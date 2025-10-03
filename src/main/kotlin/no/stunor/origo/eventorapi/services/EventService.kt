package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.data.EntryRepository
import no.stunor.origo.eventorapi.data.EventRepository
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.data.PersonRepository
import no.stunor.origo.eventorapi.exception.EntryListNotFoundException
import no.stunor.origo.eventorapi.exception.EventNotFoundException
import no.stunor.origo.eventorapi.exception.EventNotSupportedException
import no.stunor.origo.eventorapi.exception.EventorNotFoundException
import no.stunor.origo.eventorapi.exception.OrganisationNotOrganiserException
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.EventFormEnum
import no.stunor.origo.eventorapi.model.event.entry.Entry
import no.stunor.origo.eventorapi.model.event.entry.PersonEntry
import no.stunor.origo.eventorapi.model.event.entry.TeamEntry
import no.stunor.origo.eventorapi.model.person.MembershipType
import no.stunor.origo.eventorapi.model.person.Person
import no.stunor.origo.eventorapi.services.converter.EntryListConverter
import no.stunor.origo.eventorapi.services.converter.EventConverter
import no.stunor.origo.eventorapi.services.converter.OrganisationConverter
import no.stunor.origo.eventorapi.services.converter.ResultListConverter
import no.stunor.origo.eventorapi.services.converter.StartListConverter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EventService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var eventorRepository: EventorRepository

    @Autowired
    private lateinit var eventorService: EventorService
    @Autowired
    private lateinit var eventConverter: EventConverter
    @Autowired
    private lateinit var personRepository: PersonRepository
    @Autowired
    private lateinit var entryRepository: EntryRepository
    @Autowired
    private lateinit var eventRepository: EventRepository
    @Autowired
    private lateinit var organisationConverter: OrganisationConverter
    @Autowired
    private lateinit var entryListConverter: EntryListConverter
    @Autowired
    private lateinit var startListConverter: StartListConverter
    @Autowired
    private lateinit var resultListConverter: ResultListConverter


    fun getEvent(eventorId: String, eventId: String): Event {
        val eventor = eventorRepository.findByEventorId(eventorId) ?: throw EventorNotFoundException()
        val eventorEvent = eventorService.getEvent(eventor.baseUrl, eventor.eventorApiKey, eventId) ?: throw EventNotFoundException()
        val eventClassList = eventorService.getEventClasses(eventor, eventId)
        val fees = eventorService.getEventEntryFees(eventor, eventId)
        val documentList = eventorService.getEventDocuments(eventor.baseUrl, eventor.eventorApiKey, eventId)


        val organisers = organisationConverter.convertOrganisations(
            organisations = eventorEvent.organiser.organisationIdOrOrganisation,
            eventor = eventor
        )
        val event =  eventConverter.convertEvent(
            eventorEvent = eventorEvent,
            classes = eventClassList,
            fees = fees,
            documents = documentList,
            organisations = organisers,
            eventor = eventor
        )
        eventRepository.save(event)
        return event
    }

    fun getEntryList(eventorId: String, eventId: String): List<Entry> {
        val eventor = eventorRepository.findByEventorId(
            eventorId = eventorId
        ) ?: throw EventorNotFoundException()

        val resultList = eventorService.getEventResultList(
            baseUrl = eventor.baseUrl,
            apiKey = eventor.eventorApiKey,
            eventId = eventId
        )

        val startList = eventorService.getEventStartList(
            baseUrl = eventor.baseUrl,
            apiKey = eventor.eventorApiKey,
            eventId = eventId
        )

        val entryList = eventorService.getEventEntryList(
            baseUrl = eventor.baseUrl,
            apiKey = eventor.eventorApiKey,
            eventId = eventId
        ) ?: throw EntryListNotFoundException()

        var entries : MutableList<Entry> = mutableListOf<Entry>()

        when {
            resultList != null -> {
                entries.addAll(
                    resultListConverter.convertEventResultList(
                        eventor = eventor,
                        resultList = resultList
                    )
                )
            }
            startList != null -> {
                entries.addAll(
                    startListConverter.convertEventStartList(
                        eventor = eventor,
                        startList = startList
                    )
                )
            }
            !entryList.entry.isNullOrEmpty() -> {
                entries.addAll(
                    entryListConverter.convertEventEntryList(
                        eventor = eventor,
                        entryList = entryList
                    )
                )
            }
        }


        return entries
    }

    fun downloadEntries(eventorId: String, eventId: String, userId: String) {
        val persons = personRepository.findAllByUsersAndEventorId(userId, eventorId)
        var event = getEvent(
            eventorId = eventorId,
            eventId = eventId
        )

        if (event.type != EventFormEnum.Individual) {
            throw EventNotSupportedException()
        }

        /*authenticateEventOrganiser(
            event = event,
            persons = persons
        )*/

        val competitors = getEntryList(
            eventorId = eventorId,
            eventId = eventId
        )

        val existingCompetitors: MutableList<Entry> = mutableListOf()
        existingCompetitors.addAll(
            entryRepository.findAllByEventIdAndEventorId(
                eventId = eventId,
                eventorId = eventorId
            )
        )

        val result: MutableList<Entry> = mutableListOf()
        for (competitor in competitors){
            if (competitor is PersonEntry && !existingCompetitors.contains(competitor)) {
                result.add(competitor)
            }
            else if(competitor is TeamEntry && !existingCompetitors.contains(competitor)){
                result.add(competitor)
            }
        }

        entryRepository.saveAll(result)
    }

    private fun authenticateEventOrganiser(event: Event, persons: List<Person>) {
        for(organiser in event.organisers){
            for(person in persons) {
                if (person.memberships.map { it.organisationId }.contains(organiser.organisationId)
                    && (person.memberships.find { it.organisationId == organiser.organisationId }!!.type == MembershipType.Organiser
                            || person.memberships.find { it.organisationId == organiser.organisationId }!!.type == MembershipType.Admin))
                    return
            }
        }
        throw OrganisationNotOrganiserException()
    }
}