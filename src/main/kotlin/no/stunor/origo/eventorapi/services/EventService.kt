package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.data.EventRepository
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.data.FeeRepository
import no.stunor.origo.eventorapi.exception.EntryListNotFoundException
import no.stunor.origo.eventorapi.exception.EventNotFoundException
import no.stunor.origo.eventorapi.exception.EventorNotFoundException
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.Fee
import no.stunor.origo.eventorapi.model.event.entry.Entry
import no.stunor.origo.eventorapi.services.converter.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
open class EventService {

    @Autowired
    private lateinit var eventorRepository: EventorRepository
    @Autowired
    private lateinit var eventRepository: EventRepository
    @Autowired
    private lateinit var eventConverter: EventConverter
    @Autowired
    private lateinit var feeRepository: FeeRepository
    @Autowired
    private lateinit var eventorService: EventorService
    @Autowired
    private lateinit var organisationConverter: OrganisationConverter
    @Autowired
    private lateinit var entryListConverter: EntryListConverter
    @Autowired
    private lateinit var startListConverter: StartListConverter
    @Autowired
    private lateinit var resultListConverter: ResultListConverter


    @Transactional
    open fun getEvent(eventorId: String, eventId: String): Event {
        val eventor = eventorRepository.findById(eventorId).getOrNull() ?: throw EventorNotFoundException()
        val eventorEvent = eventorService.getEvent(eventor.baseUrl, eventor.eventorApiKey, eventId) ?: throw EventNotFoundException()
        val eventClassList = eventorService.getEventClasses(eventor, eventId)
        val documentList = eventorService.getEventDocuments(eventor.baseUrl, eventor.eventorApiKey, eventId)
        val existingEvent = eventRepository.findByEventorIdAndEventorRef(eventor.id, eventorEvent.eventId.content)

        val organisers = organisationConverter.convertOrganisations(
            organisations = eventorEvent.organiser.organisationIdOrOrganisation,
            eventorId = eventorId
        )
        val updatedOrNewEvent =  eventConverter.convertEvent(
            existingEvent = existingEvent,
            eventorEvent = eventorEvent,
            classes = eventClassList,
            documents = documentList,
            organisations = organisers,
            eventor = eventor
        )

        val savedEvent = eventRepository.save(updatedOrNewEvent)
        // Merge fees
        val entryFees = eventorService.getEventEntryFees(eventor, savedEvent.eventorRef)
        val convertedFees = FeeConverter.convertEntryFees(entryFees, savedEvent, eventClassList?.eventClass ?: listOf())
        val existingFees = feeRepository.findAllByEventId(savedEvent.id)
        val existingByRef = existingFees.associateBy { it.eventorRef }.toMutableMap()
        val mergedFees = mutableListOf<Fee>()
        for (fee in convertedFees) {
            val match = existingByRef[fee.eventorRef]
            if (match != null) {
                match.name = fee.name
                match.currency = fee.currency
                match.amount = fee.amount
                match.externalFee = fee.externalFee
                match.percentageSurcharge = fee.percentageSurcharge
                match.validFrom = fee.validFrom
                match.validTo = fee.validTo
                match.fromBirthYear = fee.fromBirthYear
                match.toBirthYear = fee.toBirthYear
                match.taxIncluded = fee.taxIncluded
                match.classes.clear()
                match.classes.addAll(fee.classes)
                mergedFees.add(match)
            } else {
                mergedFees.add(fee)
            }
        }
        // Remove obsolete fees
        val incomingRefs = convertedFees.map { it.eventorRef }.toSet()
        val obsolete = existingFees.filter { it.eventorRef !in incomingRefs }
        if (obsolete.isNotEmpty()) feeRepository.deleteAll(obsolete)
        feeRepository.saveAll(mergedFees)
        return savedEvent
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

    private fun mergePunchingUnits(existing: Entry, incoming: Entry) {
        when {
            existing is no.stunor.origo.eventorapi.model.event.entry.PersonEntry && incoming is no.stunor.origo.eventorapi.model.event.entry.PersonEntry -> {
                // Merge punching units uniquely by id
                val existingIds = existing.punchingUnits.map { it.id }.toMutableSet()
                for (p in incoming.punchingUnits) {
                    if (p.id.isNotBlank() && existingIds.add(p.id)) {
                        existing.punchingUnits.add(p)
                    } else // If id is blank, just add if not already present by type/id combo
                        if (p.id.isBlank() && existing.punchingUnits.none { it.type == p.type && it.id == p.id }) {
                            existing.punchingUnits.add(p)
                        }
                }
            }
            existing is no.stunor.origo.eventorapi.model.event.entry.TeamEntry && incoming is no.stunor.origo.eventorapi.model.event.entry.TeamEntry -> {
                // Merge team member punching units
                existing.teamMembers.forEach { existingMember ->
                    if (existingMember.personId != null) {
                        val incomingMember = incoming.teamMembers.find { it.personId == existingMember.personId }
                        if (incomingMember != null) {
                            val existingIds = existingMember.punchingUnits.map { it.id }.toMutableSet()
                            for (p in incomingMember.punchingUnits) {
                                if (p.id.isNotBlank() && existingIds.add(p.id)) {
                                    existingMember.punchingUnits.add(p)
                                } else if (p.id.isBlank() && existingMember.punchingUnits.none { it.type == p.type && it.id == p.id }) {
                                    existingMember.punchingUnits.add(p)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun findExisting(entries: List<Entry>, incoming: Entry): Entry? {
        return when (incoming) {
            is no.stunor.origo.eventorapi.model.event.entry.PersonEntry -> incoming.personId?.let { pid ->
                entries.firstOrNull { it is no.stunor.origo.eventorapi.model.event.entry.PersonEntry && it.personId == pid }
            }
            is no.stunor.origo.eventorapi.model.event.entry.TeamEntry -> entries.firstOrNull { it is no.stunor.origo.eventorapi.model.event.entry.TeamEntry && it.name == incoming.name }
            else -> null
        }
    }

    open fun getEntryList(eventorId: String, eventId: String): List<Entry> {
        val eventor = eventorRepository.findById(eventorId).getOrNull() ?: throw EventorNotFoundException()
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
        // Add new start entries or merge punching units
        for (entry in startEntries) {
            if (isNewEntry(entry, personIds, teamNames)) {
                entries.add(entry)
                updateEntrySets(entry, personIds, teamNames)
            } else {
                val existing = findExisting(entries, entry)
                if (existing != null) {
                    mergePunchingUnits(existing, entry)
                }
            }
        }
        // Add new entry entries or merge punching units
        for (entry in entryEntries) {
            if (isNewEntry(entry, personIds, teamNames)) {
                entries.add(entry)
                updateEntrySets(entry, personIds, teamNames)
            } else {
                val existing = findExisting(entries, entry)
                if (existing != null) {
                    mergePunchingUnits(existing, entry)
                }
            }
        }
        return entries
    }
}