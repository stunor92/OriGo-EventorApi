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
import no.stunor.origo.eventorapi.model.event.entry.PersonEntry
import no.stunor.origo.eventorapi.model.event.entry.TeamEntry
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

    private fun mergePersonPunchingUnits(existing: PersonEntry, incoming: PersonEntry) {
        if (incoming.punchingUnits.isEmpty()) return
        val seenIds = existing.punchingUnits.asSequence().map { it.id to it.type }.toMutableSet()
        for (p in incoming.punchingUnits) {
            val key = p.id to p.type
            if (p.id.isNotBlank()) {
                if (seenIds.add(key)) existing.punchingUnits.add(p)
            } else if (seenIds.add(key)) {
                existing.punchingUnits.add(p)
            }
        }
    }

    private fun mergeTeamPunchingUnits(existing: TeamEntry, incoming: TeamEntry) {
        if (incoming.teamMembers.isEmpty()) return
        // Map existing members by personId for O(1) access
        val memberByPersonId = existing.teamMembers.filter { !it.personId.isNullOrBlank() }
            .associateBy { it.personId!! }
        for (incomingMember in incoming.teamMembers) {
            val pid = incomingMember.personId ?: continue
            val existingMember = memberByPersonId[pid] ?: continue
            if (incomingMember.punchingUnits.isEmpty()) continue
            val seenIds = existingMember.punchingUnits.asSequence().map { it.id to it.type }.toMutableSet()
            for (p in incomingMember.punchingUnits) {
                val key = p.id to p.type
                if (p.id.isNotBlank()) {
                    if (seenIds.add(key)) existingMember.punchingUnits.add(p)
                } else if (seenIds.add(key)) {
                    existingMember.punchingUnits.add(p)
                }
            }
        }
    }

    private fun mergePunchingUnits(existing: Entry, incoming: Entry) {
        when (existing) {
            is PersonEntry if incoming is PersonEntry -> mergePersonPunchingUnits(existing, incoming)
            is TeamEntry if incoming is TeamEntry -> mergeTeamPunchingUnits(existing, incoming)
        }
    }

    private fun entryKey(entry: Entry): String? = when (entry) {
        is PersonEntry -> entry.personId?.takeIf { it.isNotBlank() }?.let { "PERSON:$it" }
        is TeamEntry -> entry.name.takeIf { it.isNotBlank() }?.let { "TEAM:$it" }
        else -> null
    }

    private fun keylessCompositeKey(entry: Entry): String? {
        return when (entry) {
            is PersonEntry -> if (entry.personId.isNullOrBlank()) {
                val given = entry.name.given.trim().lowercase()
                val family = entry.name.family.trim().lowercase()
                val orgRef = entry.organisation?.eventorRef?.trim()?.lowercase() ?: ""
                if (given.isEmpty() && family.isEmpty()) return null // insufficient to build key
                "P|$given|$family|$orgRef|${entry.classId}|${entry.raceId}"
            } else null
            is TeamEntry -> if (entry.name.isBlank()) {
                // Team entries without a name: use organisation refs if present
                val orgs = entry.organisations.joinToString("+") { it.eventorRef.lowercase() }
                if (orgs.isEmpty()) return null
                "T|$orgs|${entry.classId}|${entry.raceId}"
            } else null
            else -> null
        }
    }

    open fun getEntryList(eventorId: String, eventId: String): List<Entry> {
        val eventor = eventorRepository.findById(eventorId).getOrNull() ?: throw EventorNotFoundException()
        val resultEntries = fetchResultEntries(eventor, eventId)
        val startEntries = fetchStartEntries(eventor, eventId)
        val entryEntries = fetchEntryEntries(eventor, eventId)

        val entriesByKey = LinkedHashMap<String, Entry>()
        val keylessEntries = LinkedHashMap<String, Entry>() // keyed by composite name/org/class/race

        fun addOrMerge(list: List<Entry>) {
            for (e in list) {
                val key = entryKey(e)
                if (key == null) {
                    val composite = keylessCompositeKey(e) ?: continue
                    // cannot build meaningful merge key
                    val existingKeyless = keylessEntries[composite]
                    if (existingKeyless == null) {
                        keylessEntries[composite] = e
                    } else {
                        mergePunchingUnits(existingKeyless, e)
                    }
                    continue
                }
                val existing = entriesByKey[key]
                if (existing == null) {
                    entriesByKey[key] = e
                } else {
                    mergePunchingUnits(existing, e)
                }
            }
        }
        // Order: results, then start, then entry (same as before)
        addOrMerge(resultEntries)
        addOrMerge(startEntries)
        addOrMerge(entryEntries)

        val finalList = ArrayList<Entry>(entriesByKey.size + keylessEntries.size)
        finalList.addAll(entriesByKey.values)
        finalList.addAll(keylessEntries.values)
        return finalList
    }
}
