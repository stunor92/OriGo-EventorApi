package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.exception.EntryListNotFoundException
import no.stunor.origo.eventorapi.exception.EventNotFoundException
import no.stunor.origo.eventorapi.exception.EventorNotFoundException
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.entry.Entry
import no.stunor.origo.eventorapi.services.converter.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class EventService {

    @Autowired
    private lateinit var eventorRepository: EventorRepository

    @Autowired
    private lateinit var eventorService: EventorService
    @Autowired
    private lateinit var eventConverter: EventConverter
    @Autowired
    private lateinit var postgresService: PostgresService
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
        val documentList = eventorService.getEventDocuments(eventor.baseUrl, eventor.eventorApiKey, eventId)


        val organisers = organisationConverter.convertOrganisations(
            organisations = eventorEvent.organiser.organisationIdOrOrganisation,
            eventor = eventor
        )
        val event =  eventConverter.convertEvent(
            eventorEvent = eventorEvent,
            classes = eventClassList,
            documents = documentList,
            organisations = organisers,
            eventor = eventor
        )
        postgresService.runAsyncPostgresUpdates(event, eventClassList?.eventClass ?: listOf())
        return event
    }

    private fun fetchResultEntries(eventor: Eventor, eventId: String): List<Entry> {
        val resultList = eventorService.getEventResultList(eventor.baseUrl, eventor.eventorApiKey, eventId)
        return resultList?.let { resultListConverter.convertEventResultList(eventor, it) } ?: emptyList()
    }

    private fun fetchStartEntries(eventor: Eventor, eventId: String): List<Entry> {
        val startList = eventorService.getEventStartList(eventor.baseUrl, eventor.eventorApiKey, eventId)
        return startList?.let { startListConverter.convertEventStartList(eventor, it) } ?: emptyList()
    }

    private fun fetchEntryEntries(eventor: Eventor, eventId: String): List<Entry> {
        val entryList = eventorService.getEventEntryList(eventor.baseUrl, eventor.eventorApiKey, eventId)
            ?: throw EntryListNotFoundException()
        return if (!entryList.entry.isNullOrEmpty()) entryListConverter.convertEventEntryList(eventor, entryList) else emptyList()
    }

    private fun updateEntrySets(entry: Entry, personIds: MutableSet<String>, teamNames: MutableSet<String>) {
        when (entry) {
            is no.stunor.origo.eventorapi.model.event.entry.PersonEntry -> entry.personId?.let { personIds.add(it) }
            is no.stunor.origo.eventorapi.model.event.entry.TeamEntry -> teamNames.add(entry.name)
        }
    }

    private fun isNewEntry(entry: Entry, personIds: Set<String>, teamNames: Set<String>): Boolean {
        return when (entry) {
            is no.stunor.origo.eventorapi.model.event.entry.PersonEntry -> entry.personId != null && entry.personId !in personIds
            is no.stunor.origo.eventorapi.model.event.entry.TeamEntry -> entry.name.isNotEmpty() && entry.name !in teamNames
            else -> true
        }
    }

    fun getEntryList(eventorId: String, eventId: String): List<Entry> {
        val eventor = eventorRepository.findByEventorId(eventorId) ?: throw EventorNotFoundException()
        val entries: MutableList<Entry> = mutableListOf()
        val personIds: MutableSet<String> = mutableSetOf()
        val teamNames: MutableSet<String> = mutableSetOf()

        val resultEntries = fetchResultEntries(eventor, eventId)
        val startEntries = fetchStartEntries(eventor, eventId)
        val entryEntries = fetchEntryEntries(eventor, eventId)

        // Add all result entries
        for (entry in resultEntries) {
            entries.add(entry)
            updateEntrySets(entry, personIds, teamNames)
        }
        // Add new start entries
        for (entry in startEntries) {
            if (isNewEntry(entry, personIds, teamNames)) {
                entries.add(entry)
                updateEntrySets(entry, personIds, teamNames)
            }
        }
        // Add new entry entries
        for (entry in entryEntries) {
            if (isNewEntry(entry, personIds, teamNames)) {
                entries.add(entry)
            }
        }
        return entries
    }
}