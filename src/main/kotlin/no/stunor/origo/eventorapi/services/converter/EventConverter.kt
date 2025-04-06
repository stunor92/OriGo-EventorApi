package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.Region
import no.stunor.origo.eventorapi.model.event.*
import no.stunor.origo.eventorapi.model.organisation.Organisation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.ParseException

@Component
class EventConverter {
    @Autowired
    private lateinit var eventClassConverter: EventClassConverter

    @Autowired
    private lateinit var entryConverter: EntryConverter

    @Autowired
    private lateinit var competitorConverter: CompetitorConverter

    @Autowired
    private lateinit var timeStampConverter: TimeStampConverter

    fun convertEventClassification(eventForm: String?): EventClassificationEnum {
        return when (eventForm) {
            "1" -> EventClassificationEnum.Championship
            "2" -> EventClassificationEnum.National
            "3" -> EventClassificationEnum.Regional
            "4" -> EventClassificationEnum.Local
            else -> EventClassificationEnum.Club
        }
    }


    fun convertEventStatus(eventStatusId: String?): EventStatusEnum {
        return when (eventStatusId) {
            "2" -> EventStatusEnum.RegionApproved
            "3" -> EventStatusEnum.Approved
            "4" -> EventStatusEnum.Created
            "5" -> EventStatusEnum.EntryOpen
            "6" -> EventStatusEnum.EntryPaused
            "7" -> EventStatusEnum.EntryClosed
            "8" -> EventStatusEnum.Live
            "9" -> EventStatusEnum.Completed
            "10" -> EventStatusEnum.Canceled
            "11" -> EventStatusEnum.Reported
            else -> EventStatusEnum.Applied
        }
    }

    fun convertOrganisers(organisers: List<Any>): List<String> {
        val result: MutableList<String> = ArrayList()
        for (organiser in organisers) {
            if (organiser is org.iof.eventor.Organisation) {
                if (organiser.organisationId != null) result.add(organiser.organisationId.content)
            } else if (organiser is org.iof.eventor.OrganisationId) {
                result.add(organiser.content)
            }
        }
        return result
    }

    fun convertEventDisciplines(disciplineIds: List<Any>): List<DisciplineEnum> {
        val result: MutableList<DisciplineEnum> = ArrayList()
        for (disciplineId in disciplineIds) {
            result.add(convertEventDiscipline(disciplineId))
        }
        return result
    }

    fun convertEventForm(eventForm: String?): EventFormEnum {
        return when (eventForm) {
            "IndSingleDay", "IndMultiDay" -> EventFormEnum.Individual
            "RelaySingleDay", "RelayMultiDay" -> EventFormEnum.Relay
            "PatrolSingleDay", "PatrolMultiDay" -> EventFormEnum.Patrol
            "TeamSingleDay", "TeamMultiDay" -> EventFormEnum.Team
            else -> EventFormEnum.Individual
        }
    }

    private fun convertEventDiscipline(disciplineId: Any): DisciplineEnum {
        return when ((disciplineId as org.iof.eventor.DisciplineId).content) {
            "1" -> DisciplineEnum.FootO
            "2" -> DisciplineEnum.MtbO
            "3" -> DisciplineEnum.SkiO
            "4" -> DisciplineEnum.PreO
            else -> DisciplineEnum.FootO
        }
    }


    @Throws(ParseException::class)
    fun convertEvent(
        event: org.iof.eventor.Event,
        eventCLassList: org.iof.eventor.EventClassList?,
        documentList: org.iof.eventor.DocumentList?,
        organisations: List<Organisation>,
        regions: List<Region>,
        eventor: Eventor
    ): Event {
        return Event(
            eventorId = eventor.eventorId,
            eventId = event.eventId.content,
            name = event.name.content,
            type = convertEventForm(event.eventForm),
            classification = convertEventClassification(event.eventClassificationId.content),
            status = convertEventStatus(event.eventStatusId.content),
            disciplines = convertEventDisciplines(event.disciplineIdOrDiscipline),
            startDate = timeStampConverter.parseTimestamp("${event.startDate.date.content} ${event.startDate.clock.content}", eventor),
            finishDate = timeStampConverter.parseTimestamp("${event.finishDate.date.content} ${event.finishDate.clock.content}", eventor),
            organisers = organisations.map { it.organisationId },
            regions = regions.map { it.regionId },
            eventClasses = eventClassConverter.convertEventClasses(eventCLassList),
            documents = convertEventDocument(documentList),
            entryBreaks = entryConverter.convertEntryBreaks(event.entryBreak, eventor),
            races = convertRaces(event, event.eventRace, eventor),
            punchingUnitTypes = convertPunchingUnitTypes(event.punchingUnitType),
            webUrls = null,
            message = convertHostMessage(event.hashTableEntry),
            email = null,
            phone = null
        )
    }

    private fun convertRaces(
        event: org.iof.eventor.Event,
        eventRaces: List<org.iof.eventor.EventRace>,
        eventor: Eventor
    ): List<Race> {
        val result  = mutableListOf<Race>()

        for (eventRace in eventRaces) {
            result.add(convertRace(event, eventRace, eventor))
        }
        return result
    }

    private fun convertRace(
        event: org.iof.eventor.Event,
        eventRace: org.iof.eventor.EventRace,
        eventor: Eventor
    ): Race {
        return Race(
            raceId = eventRace.eventRaceId.content,
            name = eventRace.name.content,
            lightCondition = convertLightCondition(eventRace.raceLightCondition),
            distance = convertRaceDistance(eventRace.raceDistance),
            date = if (eventRace.raceDate != null) timeStampConverter.parseTimestamp("${eventRace.raceDate.date.content} ${eventRace.raceDate.clock.content}", eventor) else null,
            position = if (eventRace.eventCenterPosition != null) convertPosition(eventRace.eventCenterPosition) else null,
            startList = hasStartList(event.hashTableEntry, eventRace.eventRaceId.content),
            resultList = hasResultList(event.hashTableEntry, eventRace.eventRaceId.content),
            livelox = hasLivelox(event.hashTableEntry)
        )
    }


    fun convertLightCondition(lightCondition: String?): LightConditionEnum {
        return when (lightCondition) {
            "Day" -> LightConditionEnum.Day
            "Night" -> LightConditionEnum.Night
            "DayAndNight" -> LightConditionEnum.DayAndNight
            else -> LightConditionEnum.Day
        }
    }


    fun convertRaceDistance(raceDistance: String?): DistanceEnum {
        if (raceDistance == null) {
            return DistanceEnum.Middle
        }
        return when (raceDistance) {
            "Long" -> DistanceEnum.Long
            "Middle" -> DistanceEnum.Middle
            "Sprint", "SprintRelay" -> DistanceEnum.Sprint
            "Ultralong" -> DistanceEnum.UltraLong
            "Pre-O" -> DistanceEnum.PreO
            "Temp-O" -> DistanceEnum.TempO
            else -> DistanceEnum.Middle
        }
    }


    fun hasStartList(hashTableEntries: List<org.iof.eventor.HashTableEntry>, eventRaceId: String): Boolean {
        for (hashTableEntry in hashTableEntries) {
            if (hashTableEntry.key.content == "startList_$eventRaceId") {
                return true
            }
        }
        return false
    }

    fun hasResultList(hashTableEntries: List<org.iof.eventor.HashTableEntry>, eventRaceId: String): Boolean {
        for (hashTableEntry in hashTableEntries) {
            if (hashTableEntry.key.content == "officialResult_$eventRaceId") {
                return true
            }
        }
        return false
    }

    fun hasLivelox(hashTableEntries: List<org.iof.eventor.HashTableEntry>): Boolean {
        for (hashTableEntry in hashTableEntries) {
            if (hashTableEntry.key.content == "Eventor_LiveloxEventConfigurations") {
                return true
            }
        }
        return false
    }


    fun convertPosition(eventCenterPosition: org.iof.eventor.EventCenterPosition): RacePosition {
        return RacePosition(eventCenterPosition.y.toDouble(), eventCenterPosition.x.toDouble())
    }

    private fun convertHostMessage(hashTableEntries: List<org.iof.eventor.HashTableEntry>): String? {
        for (hashTableEntry in hashTableEntries) {
            if (hashTableEntry.key.content == "Eventor_Message") {
                return hashTableEntry.value.content
            }
        }
        return null
    }


    private fun convertPunchingUnitTypes(punchingUnitTypes: List<org.iof.eventor.PunchingUnitType>): List<PunchingUnitType> {
        val result: MutableList<PunchingUnitType> = ArrayList()
        for (punchingUnitType in punchingUnitTypes) {
            result.add(competitorConverter.convertPunchingUnitType(punchingUnitType.value))
        }
        return result
    }


    private fun convertEventDocument(documentList: org.iof.eventor.DocumentList?): List<EventorDocument> {
        val result: MutableList<EventorDocument> = ArrayList()
        if (documentList == null) {
            return listOf()
        }
        for (document in documentList.document) {
            result.add(EventorDocument(document.name, document.url, document.type))
        }
        return result
    }

}
