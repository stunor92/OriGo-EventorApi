package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.data.CompetitorRepository
import no.stunor.origo.eventorapi.data.EntryFeesRepository
import no.stunor.origo.eventorapi.data.EventRepository
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.data.OrganisationRepository
import no.stunor.origo.eventorapi.data.PersonRepository
import no.stunor.origo.eventorapi.data.RegionRepository
import no.stunor.origo.eventorapi.exception.EntryListNotFoundException
import no.stunor.origo.eventorapi.exception.EventNotFoundException
import no.stunor.origo.eventorapi.exception.EventorNotFoundException
import no.stunor.origo.eventorapi.exception.EventorParsingException
import no.stunor.origo.eventorapi.exception.OrganisationNotOrganiserException
import no.stunor.origo.eventorapi.exception.ResultListNotFoundException
import no.stunor.origo.eventorapi.exception.StartListNotFoundException
import no.stunor.origo.eventorapi.model.Region
import no.stunor.origo.eventorapi.model.event.EntryFee
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.competitor.Competitor
import no.stunor.origo.eventorapi.model.event.competitor.PersonCompetitor
import no.stunor.origo.eventorapi.model.event.competitor.TeamCompetitor
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.person.MembershipType
import no.stunor.origo.eventorapi.model.person.Person
import no.stunor.origo.eventorapi.services.converter.EntryFeeConverter
import no.stunor.origo.eventorapi.services.converter.EntryListConverter
import no.stunor.origo.eventorapi.services.converter.EventConverter
import no.stunor.origo.eventorapi.services.converter.ResultListConverter
import no.stunor.origo.eventorapi.services.converter.StartListConverter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.ParseException

@Service
class EventService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var eventorRepository: EventorRepository
    @Autowired
    private lateinit var regionRepository: RegionRepository
    @Autowired
    private lateinit var eventorService: EventorService
    @Autowired
    private lateinit var eventConverter: EventConverter
    @Autowired
    private lateinit var organisationRepository: OrganisationRepository
    @Autowired
    private lateinit var personRepository: PersonRepository
    @Autowired
    private lateinit var competitorsRepository: CompetitorRepository
    @Autowired
    private lateinit var eventRepository: EventRepository
    @Autowired
    private lateinit var entryFeesRepository: EntryFeesRepository
    @Autowired
    private lateinit var entryListConverter: EntryListConverter
    @Autowired
    private lateinit var startListConverter: StartListConverter
    @Autowired
    private lateinit var resultListConverter: ResultListConverter
    @Autowired
    private lateinit var entryFeeConverter: EntryFeeConverter

    fun getEvent(eventorId: String, eventId: String): Event {
        val eventor = eventorRepository.findByEventorId(eventorId) ?: throw EventorNotFoundException()
        val event = eventorService.getEvent(eventor.baseUrl, eventor.apiKey, eventId) ?: throw EventNotFoundException()
        val eventClassList = eventorService.getEventClasses(eventor, eventId)

        val documentList = eventorService.getEventDocuments(eventor.baseUrl, eventor.apiKey, eventId)

        val organisers: MutableList<Organisation> = ArrayList()
        val regions: MutableList<Region> = ArrayList()

        for (o in event.organiser.organisationIdOrOrganisation) {
            val org = o as org.iof.eventor.Organisation
            organisationRepository.findByOrganisationIdAndEventorId(organisationId = org.organisationId.content, eventorId = eventor.eventorId)?.let { organisers.add(it) }
            var region: Region? = null
            if (org.parentOrganisation.organisationId != null) {
                region = regionRepository.findByRegionIdAndEventorId(org.parentOrganisation.organisationId.content, eventorId)
            }
            if (region == null) {
                log.info("{} does not have a region. check if {} is a region.", org.name.content, org.name.content)

                try {
                    region = regionRepository.findByRegionIdAndEventorId(org.organisationId.content, eventorId)
                } catch (e1: Exception) {
                    log.info("Region {} does not exist.", org.organisationId.content)
                }
            }
            var regionExist = false

            for ((_, regionId) in regions) {
                if (region != null && region.regionId == regionId) {
                    regionExist = true
                }
            }
            if (!regionExist && region != null) {
                regions.add(region)
            }
        }
        return eventConverter.convertEvent(event, eventClassList, documentList, organisers, regions, eventor)
    }

    fun getEntryList(eventorId: String, eventId: String): List<Competitor> {
        val eventor = eventorRepository.findByEventorId(eventorId) ?: throw EventorNotFoundException()
        val entryList = eventorService.getEventEntryList(eventor.baseUrl, eventor.apiKey, eventId) ?: throw EntryListNotFoundException()
        return entryListConverter.convertEventEntryList(entryList)
    }

    fun getStartList(eventorId: String, eventId: String): List<Competitor> {
        val eventor = eventorRepository.findByEventorId(eventorId) ?: throw EventorNotFoundException()
        val startList = eventorService.getEventStartList(eventor.baseUrl, eventor.apiKey, eventId) ?: throw StartListNotFoundException()
        return startListConverter.convertEventStartList(eventor, startList)
    }

    fun getResultList(eventorId: String, eventId: String): List<Competitor> {
        val eventor = eventorRepository.findByEventorId(eventorId) ?: throw EventorNotFoundException()
        val resultList = eventorService.getEventResultList(eventor.baseUrl, eventor.apiKey, eventId) ?: throw ResultListNotFoundException()
        try {
            return resultListConverter.convertEventResultList(eventor, resultList)
        } catch (e: NumberFormatException) {
            log.warn(e.message)
            throw EventorParsingException()
        } catch (e: ParseException) {
            log.warn(e.message)
            throw EventorParsingException()
        }
    }

    private fun getEntryFees(eventorId: String, event: Event): List<EntryFee> {
        val eventor = eventorRepository.findByEventorId(eventorId) ?: throw EventorNotFoundException()
        val entryFees = eventorService.getEventEntryFees(eventor.baseUrl, eventor.apiKey, event.eventId) ?: throw EntryListNotFoundException()
        return entryFeeConverter.convertEventEntryFees(entryFees, event, eventor)
    }

    fun downloadEvent(eventorId: String, eventId: String) {
        val event = getEvent(eventorId = eventorId, eventId = eventId)
        val existingEvent = eventRepository.findByEventIdAndEventorId(eventId = eventId, eventorId = eventorId)
        if(existingEvent != null) {
            event.id = existingEvent.id
        }
        eventRepository.save(event)
    }

    fun downloadEntryFees(eventorId: String, eventId: String) {
        val event = eventRepository.findByEventIdAndEventorId(eventId = eventId, eventorId = eventorId) ?: throw EventNotFoundException()
        val entryFees = getEntryFees(eventorId = eventorId, event = event)
        val existingFees = entryFeesRepository.findAllForEvent(event)

        for (entryFee in entryFees){
            if (existingFees.any { it.entryFeeId == entryFee.entryFeeId }) {
                entryFee.id = existingFees.first{ it.entryFeeId == entryFee.entryFeeId }.id
            }
        }

        event.id?.let { entryFeesRepository.saveAll(it, entryFees) }
    }

    fun downloadCompetitors(eventorId: String, eventId: String, userId: String) {
        val persons = personRepository.findAllByUserIdAndEventorId(userId, eventorId)
        var event = getEvent(eventorId = eventorId, eventId = eventId)
        authenticateEventOrganiser(event = event, persons = persons)
        val competitors = getEntryList(eventorId = eventorId, eventId = eventId)
        val existingCompetitors: MutableList<Competitor> = mutableListOf()
        existingCompetitors.addAll(competitorsRepository.findAllByEventIdAndEventorId(eventId = eventId, eventorId = eventorId))

        val result: MutableList<Competitor> = mutableListOf()
        for (competitor in competitors){
            if(competitor is PersonCompetitor) {
                if (!existingCompetitors.contains(competitor)) {
                    result.add(competitor)
                }
            } else if(competitor is TeamCompetitor){
                if(!existingCompetitors.contains(competitor)){
                    result.add(competitor)
                }
            }
        }
        if (event.id == null){
            event = eventRepository.findByEventIdAndEventorId(eventId = eventId, eventorId = eventorId) ?: event
        }

        event.id?.let { competitorsRepository.saveAll(it, result) }
    }

    private fun authenticateEventOrganiser(event: Event, persons: List<Person>) {
        for(organiser in event.organisers){
            for(person in persons) {
                if (person.memberships.containsKey(organiser.organisationId))
                    if (person.memberships[organiser.organisationId] == MembershipType.Organiser || person.memberships[organiser.organisationId] == MembershipType.Admin)
                        return
            }
        }
        throw OrganisationNotOrganiserException()
    }
}