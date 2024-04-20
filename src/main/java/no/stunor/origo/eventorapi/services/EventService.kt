package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.api.exception.EntryListNotFoundException
import no.stunor.origo.eventorapi.api.exception.EventNotFoundException
import no.stunor.origo.eventorapi.api.exception.EventorParsingException
import no.stunor.origo.eventorapi.api.exception.ResultListNotFoundException
import no.stunor.origo.eventorapi.api.exception.StartListNotFoundException
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.data.OrganisationRepository
import no.stunor.origo.eventorapi.data.RegionRepository
import no.stunor.origo.eventorapi.model.Region
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.origo.entry.EventEntryList
import no.stunor.origo.eventorapi.model.origo.result.RaceResultList
import no.stunor.origo.eventorapi.model.origo.start.RaceStartList
import no.stunor.origo.eventorapi.model.calendar.UserRace
import no.stunor.origo.eventorapi.services.converter.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.ParseException
import java.util.*

@Service
class EventService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var eventorRepository: EventorRepository

    @Autowired
    private lateinit var regionRepository: RegionRepository

    @Autowired
    private lateinit var userEntryService: UserEntryService

    @Autowired
    private lateinit var eventorService: EventorService

    @Autowired
    private lateinit var eventConverter: EventConverter

    @Autowired
    private lateinit var organisationRepository: OrganisationRepository

    @Autowired
    private lateinit var entryListConverter: EntryListConverter
    @Autowired
    private lateinit var startListConverter: StartListConverter
    @Autowired
    private lateinit var resultListConverter: ResultListConverter


    fun getEvent(eventorId: String, eventId: String, userId: String?): Event {
        val eventor = Objects.requireNonNull(eventorRepository.findByEventorId(eventorId))?.block()

        val event = eventorService.getEvent(eventor!!.baseUrl, eventor.apiKey, eventId) ?: throw EventNotFoundException()
        val eventClassList = eventorService.getEventClasses(eventor, eventId)
        val documentList = eventorService.getEventDocuments(eventor.baseUrl, eventor.apiKey, eventId)

        val organisers: MutableList<Organisation> = ArrayList()
        val regions: MutableList<Region> = ArrayList()

        for (o in event.organiser.organisationIdOrOrganisation) {
            val org = o as org.iof.eventor.Organisation
            organisationRepository.findByOrganisationIdAndEventorId(organisationId = org.organisationId.content, eventorId = eventor.eventorId).block()?.let { organisers.add(it) }
            var region: Region? = null
            if (org.parentOrganisation.organisationId != null) {
                region = regionRepository.findByRegionIdAndEventorId(org.parentOrganisation.organisationId.content, eventorId).block()
            }
            if (region == null) {
                log.info("{} does not have a region. check if {} is a region.", org.name.content, org.name.content)

                try {
                    region = regionRepository.findByRegionIdAndEventorId(org.organisationId.content, eventorId).block()
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

        var raceCompetitors: List<UserRace>? = ArrayList()
        if (userId != null) {
            raceCompetitors = userEntryService.userRaces(userId, eventor, eventId)
        }
        return eventConverter.convertEvent(event, eventClassList, documentList, organisers, regions, eventor, raceCompetitors)
    }

    fun getEntryList(eventorId: String, eventId: String): EventEntryList {
        val eventor = eventorRepository.findByEventorId(eventorId).block()!!
        val entryList = eventorService.getEventEntryList(eventor.baseUrl, eventor.apiKey, eventId) ?: throw EntryListNotFoundException()
        return entryListConverter.convertEventEntryList(entryList, eventor)
    }

    fun getStartList(eventorId: String, eventId: String): List<RaceStartList> {
        val eventor = eventorRepository.findByEventorId(eventorId).block()!!
        val startList = eventorService.getEventStartList(eventor.baseUrl, eventor.apiKey, eventId) ?: throw StartListNotFoundException()
        return startListConverter.convertEventStartList(startList, eventor)
    }

    fun getResultList(eventorId: String, eventId: String): List<RaceResultList> {
        val eventor = eventorRepository.findByEventorId(eventorId).block()!!
        val resultList = eventorService.getEventResultList(eventor.baseUrl, eventor.apiKey, eventId) ?: throw ResultListNotFoundException()
        try {
            return resultListConverter.convertEventResultList(resultList, eventor)
        } catch (e: NumberFormatException) {
            log.warn(e.message)
            throw EventorParsingException()
        } catch (e: ParseException) {
            log.warn(e.message)
            throw EventorParsingException()
        }
    }
}