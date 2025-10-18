package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.exception.EntryListNotFoundException
import no.stunor.origo.eventorapi.exception.EventNotFoundException
import no.stunor.origo.eventorapi.exception.EventorNotFoundException
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

        val entries : MutableList<Entry> = mutableListOf()

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
}