package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.*
import no.stunor.origo.eventorapi.model.organisation.Organisation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.text.ParseException

@Component
class EventConverter {
    @Autowired
    private lateinit var entryListConverter: EntryListConverter

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
        documents: org.iof.eventor.DocumentList?,
        organisations: MutableList<Organisation>,
        eventor: Eventor
    ): Event {
        val event = Event(
            eventorId = eventor.eventorId,
            eventor = eventor,
            eventId = eventorEvent.eventId.content,
            name = eventorEvent.name.content,
            type = convertEventForm(eventorEvent.eventForm),
            classification = convertEventClassification(eventorEvent.eventClassificationId.content),
            status = convertEventStatus(eventorEvent.eventStatusId.content),
            disciplines = convertEventDisciplines(eventorEvent.disciplineIdOrDiscipline).toTypedArray(),
            startDate = TimeStampConverter.parseDate("${eventorEvent.startDate.date.content} ${eventorEvent.startDate.clock.content}", eventor),
            finishDate = TimeStampConverter.parseDate("${eventorEvent.finishDate.date.content} ${eventorEvent.finishDate.clock.content}", eventor),
            organisers = organisations,
            entryBreaks = convertEntryBreaks(eventorEvent.entryBreak, eventor),
            punchingUnitTypes = entryListConverter.convertPunchingUnitTypes(eventorEvent.punchingUnitType).toTypedArray(),
            webUrls = listOf(),
            message = convertHostMessage(eventorEvent.hashTableEntry),
            email = null,
            phone = null
        )
        event.classes = EventClassConverter.convertEventClasses(eventCLassList = classes, eventor = eventor, event = event)
        event.races = convertRaces(event, eventorEvent.hashTableEntry, eventorEvent.eventRace, eventor)
        event.documents = convertEventDocument(documents, event= event, eventorId = eventor.eventorId)
        return event

    }

    private fun convertRaces(
        event: Event,
        hashTableEntries: List<org.iof.eventor.HashTableEntry>,
        eventRaces: List<org.iof.eventor.EventRace>,
        eventor: Eventor
    ): MutableList<Race> {
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
        hashTableEntries: List<org.iof.eventor.HashTableEntry>,
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
            date = if (eventRace.raceDate != null) TimeStampConverter.parseDate("${eventRace.raceDate.date.content} ${eventRace.raceDate.clock.content}", eventor) else null,
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


    private fun convertEventDocument(documentList: org.iof.eventor.DocumentList?, event: Event, eventorId: String): MutableList<Document> {
        val result: MutableList<Document> = ArrayList()
        if (documentList == null) {
            return mutableListOf()
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

    fun convertEntryBreaks(
        entryBreaks: List<org.iof.eventor.EntryBreak>,
        eventor: Eventor
    ): List<Timestamp> {
        val result: MutableList<Timestamp> = ArrayList()
        for (entryBreak in entryBreaks) {
            if (entryBreak.validToDate != null) {
                result.add(
                    TimeStampConverter.parseDate(
                        "${entryBreak.validToDate.date.content} ${entryBreak.validToDate.clock.content}",
                        eventor
                    )
                )
            }
        }
        return result
    }

}
