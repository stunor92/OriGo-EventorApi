package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.Region
import no.stunor.origo.eventorapi.model.event.*
import no.stunor.origo.eventorapi.model.organisation.Organisation
import org.iof.eventor.HashTableEntry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.ParseException

@Component
class EventConverter {
    @Autowired
    private lateinit var eventClassConverter: EventClassConverter

    @Autowired
    private lateinit var feeConverter: FeeConverter

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

    fun convertEventDisciplines(disciplineIds: List<Any>): List<Discipline> {
        val result: MutableList<Discipline> = ArrayList()
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

    private fun convertEventDiscipline(disciplineId: Any): Discipline {
        return when ((disciplineId as org.iof.eventor.DisciplineId).content) {
            "1" -> Discipline.FootO
            "2" -> Discipline.MtbO
            "3" -> Discipline.SkiO
            "4" -> Discipline.PreO
            else -> Discipline.FootO
        }
    }


    @Throws(ParseException::class)
    fun convertEvent(
        eventorEvent: org.iof.eventor.Event,
        classes: org.iof.eventor.EventClassList?,
        fees: org.iof.eventor.EntryFeeList?,
        documents: org.iof.eventor.DocumentList?,
        organisations: List<Organisation>,
        regions: List<Region>,
        eventor: Eventor
    ): Event {
        var event = Event(
            eventorId = eventor.eventorId,
            eventId = eventorEvent.eventId.content,
            name = eventorEvent.name.content,
            type = convertEventForm(eventorEvent.eventForm),
            classification = convertEventClassification(eventorEvent.eventClassificationId.content),
            status = convertEventStatus(eventorEvent.eventStatusId.content),
            disciplines = convertEventDisciplines(eventorEvent.disciplineIdOrDiscipline).toTypedArray(),
            startDate = timeStampConverter.parseDate("${eventorEvent.startDate.date.content} ${eventorEvent.startDate.clock.content}", eventor),
            finishDate = timeStampConverter.parseDate("${eventorEvent.finishDate.date.content} ${eventorEvent.finishDate.clock.content}", eventor),
            organisers = organisations.map { it.organisationId },
            regions = regions.map { it.regionId },
            entryBreaks = feeConverter.convertEntryBreaks(eventorEvent.entryBreak, eventor).toTypedArray(),
            punchingUnitTypes = convertPunchingUnitTypes(eventorEvent.punchingUnitType).toTypedArray(),
            webUrls = listOf(),
            message = convertHostMessage(eventorEvent.hashTableEntry),
            email = null,
            phone = null
        )
        event.classes = eventClassConverter.convertEventClasses(eventCLassList = classes, eventor = eventor, event = event)
        event.races = convertRaces(event, eventorEvent.hashTableEntry, eventorEvent.eventRace, eventor)
        event.documents = convertEventDocument(documents, event= event, eventorId = eventor.eventorId)
        event.fees = feeConverter.convertEntryFees(entryFees = fees, eventor = eventor, event = event, eventClasses = classes)
        return event

    }

    private fun convertRaces(
        event: Event,
        hashTableEntries: List<HashTableEntry>,
        eventRaces: List<org.iof.eventor.EventRace>,
        eventor: Eventor
    ): List<Race> {
        val result  = mutableListOf<Race>()

        for (eventRace in eventRaces) {
            result.add(convertRace(
                event= event,
                hashTableEntries = hashTableEntries,
                eventRace = eventRace,
                eventor = eventor
            ))
        }
        return result
    }

    private fun convertRace(
        event: Event,
        hashTableEntries: List<HashTableEntry>,
        eventRace: org.iof.eventor.EventRace,
        eventor: Eventor
    ): Race {
        return Race(
            eventorId = eventor.eventorId,
            eventId = event.eventId,
            raceId = eventRace.eventRaceId.content,
            name = eventRace.name.content,
            lightCondition = convertLightCondition(eventRace.raceLightCondition),
            distance = convertRaceDistance(eventRace.raceDistance),
            date = if (eventRace.raceDate != null) timeStampConverter.parseDate("${eventRace.raceDate.date.content} ${eventRace.raceDate.clock.content}", eventor) else null,
            position = if (eventRace.eventCenterPosition != null) convertPosition(eventRace.eventCenterPosition) else null,
            startList = hasStartList(hashTableEntries, eventRace.eventRaceId.content),
            resultList = hasResultList(hashTableEntries, eventRace.eventRaceId.content),
            livelox = hasLivelox(hashTableEntries),
            event = event,
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


    private fun convertEventDocument(documentList: org.iof.eventor.DocumentList?, event: Event, eventorId: String): List<Document> {
        val result: MutableList<Document> = ArrayList()
        if (documentList == null) {
            return listOf()
        }
        for (document in documentList.document) {
            result.add(
                Document(
                    eventorId = eventorId,
                    eventId = event.eventId,
                    documentId = document.id.toString(),
                    name = document.name,
                    url = document.url,
                    type = document.type,
                    event = event
                )
            )
        }
        return result
    }

}
