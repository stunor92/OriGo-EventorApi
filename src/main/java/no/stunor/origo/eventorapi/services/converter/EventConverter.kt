package no.stunor.origo.eventorapi.services.converter

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.data.OrganisationRepository
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.Region
import no.stunor.origo.eventorapi.model.event.*
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.event.CCard
import no.stunor.origo.eventorapi.model.event.Position
import no.stunor.origo.eventorapi.model.origo.entry.EntryBreak
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.collections.ArrayList

@Component
class EventConverter {
    @Autowired
    private lateinit var organisationRepository: OrganisationRepository

    @Autowired
    private lateinit var eventClassConverter: EventClassConverter
    fun convertEventClassification(eventForm: String?): EventClassificationEnum {
        return when (eventForm) {
            "1" -> EventClassificationEnum.CHAMPIONSHIP
            "2" -> EventClassificationEnum.NATIONAL
            "3" -> EventClassificationEnum.REGIONAL
            "4" -> EventClassificationEnum.LOCAL
            else -> EventClassificationEnum.CLUB
        }
    }


    fun convertEventStatus(eventStatusId: String?): EventStatusEnum {
        return when (eventStatusId) {
            "2" -> EventStatusEnum.REGIONAPPROVED
            "3" -> EventStatusEnum.APPROVED
            "4" -> EventStatusEnum.CREATED
            "5" -> EventStatusEnum.ENTRYOPEN
            "6" -> EventStatusEnum.ENTRYPAUSED
            "7" -> EventStatusEnum.ENTRYCLOSED
            "8" -> EventStatusEnum.LIVE
            "9" -> EventStatusEnum.COMPLETED
            "10" -> EventStatusEnum.CANCELED
            "11" -> EventStatusEnum.REPORTED
            else -> EventStatusEnum.APPLIED
        }
    }

    fun convertRaceDateWithoutTime(startTime: org.iof.eventor.RaceDate): Date? {
        val dateString = startTime.date.content
        val parser = SimpleDateFormat("yyyy-MM-dd")
        parser.timeZone = TimeZone.getTimeZone("UTC")

        return try {
            parser.parse(dateString)
        } catch (e: ParseException) {
            null
        }
    }

    fun convertOrganisers(eventor: Eventor, organisers: List<Any>): List<Organisation> {
        val result: MutableList<Organisation> = ArrayList()
        for (organiser in organisers) {
            if(organiser is org.iof.eventor.Organisation){
                val o = organisationRepository.findByOrganisationIdAndEventorId(organiser.organisationId.content, eventor.eventorId).block()
                if (o != null) result.add(o)
            } else if(organiser is org.iof.eventor.OrganisationId){
                val o = organisationRepository.findByOrganisationIdAndEventorId(organiser.content, eventor.eventorId).block()
                if (o != null) result.add(o)
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
            "IndSingleDay", "IndMultiDay" -> EventFormEnum.INDIVIDUAL
            "RelaySingleDay", "RelayMultiDay" -> EventFormEnum.RELAY
            "PatrolSingleDay", "PatrolMultiDay" -> EventFormEnum.PATROL
            "TeamSingleDay", "TeamMultiDay" -> EventFormEnum.TEAM
            else -> EventFormEnum.INDIVIDUAL
        }
    }

    private fun convertEventDiscipline(disciplineId: Any): DisciplineEnum {
        return when ((disciplineId as org.iof.eventor.DisciplineId).content) {
            "1" -> DisciplineEnum.FOOT
            "2" -> DisciplineEnum.MTB
            "3" -> DisciplineEnum.SKI
            "4" -> DisciplineEnum.PRE
            else -> DisciplineEnum.FOOT
        }
    }

    fun convertEntryBreaks(entryBreaks: List<org.iof.eventor.EntryBreak>): List<EntryBreak> {
        val result: MutableList<EntryBreak> = ArrayList()
        for (entryBreak in entryBreaks) {
            result.add(convertEntryBreak(entryBreak))
        }
        return result
    }

    @Throws(ParseException::class)
    fun convertEvent(
            event: org.iof.eventor.Event,
            eventCLassList: org.iof.eventor.EventClassList?,
            documentList: org.iof.eventor.DocumentList?,
            organisations: List<Organisation>,
            regions: List<Region>,
            eventor: Eventor): Event {
        return Event(
                id = null,
                eventorId = eventor.eventorId,
                eventId = event.eventId.content,
                name = event.name.content,
                type = convertEventForm(event.eventForm),
                classification = convertEventClassification(event.eventClassificationId.content),
                status = convertEventStatus(event.eventStatusId.content),
                disciplines = convertEventDisciplines(event.disciplineIdOrDiscipline),
                startDate = convertStartDate(event.startDate),
                finishDate = convertFinishDate(event.finishDate),
                organisers = organisations,
                regions = regions,
                eventClasses = eventClassConverter.convertEventClasses(eventCLassList),
                documents = convertEventDocument(documentList),
                entryBreaks = convertEntryBreaks(event.entryBreak),
                races = convertRaces(event, event.eventRace),
                punchingUnitTypes = convertPunchingUnitTypes(event.punchingUnitType),
                webUrls = null,
                message = convertHostMessage(event.hashTableEntry),
                email = null,
                phone = null
        )
    }

    private fun convertRaces(event: org.iof.eventor.Event, eventRaces: List<org.iof.eventor.EventRace>): List<Race> {
        val result: MutableList<Race> = ArrayList()

        for (eventRace in eventRaces) {
            result.add(convertRace(event, eventRace))
        }
        return result
    }

    private fun convertRace(event: org.iof.eventor.Event, eventRace: org.iof.eventor.EventRace): Race {
        return Race(
                raceId = eventRace.eventRaceId.content,
                name = eventRace.name.content,
                lightCondition = convertLightCondition(eventRace.raceLightCondition),
                distance = convertRaceDistance(eventRace.raceDistance),
                date = if (eventRace.raceDate != null) convertRaceDate(eventRace.raceDate) else null,
                position = if (eventRace.eventCenterPosition != null) convertPosition(eventRace.eventCenterPosition) else null,
                startList = hasStartList(event.hashTableEntry, eventRace.eventRaceId.content),
                resultList = hasResultList(event.hashTableEntry, eventRace.eventRaceId.content),
                livelox = hasLivelox(event.hashTableEntry)
        )
    }


    fun convertLightCondition(lightCondition: String?): LightConditionEnum {
        return when (lightCondition) {
            "Day" -> LightConditionEnum.DAY
            "Night" -> LightConditionEnum.NIGHT
            "DayAndNight" -> LightConditionEnum.COMBINED
            else -> LightConditionEnum.DAY
        }
    }


    fun convertRaceDistance(raceDistance: String?): DistanceEnum {
        if (raceDistance == null) {
            return DistanceEnum.MIDDLE
        }
        return when (raceDistance) {
            "Long" -> DistanceEnum.LONG
            "Middle" -> DistanceEnum.MIDDLE
            "Sprint", "SprintRelay" -> DistanceEnum.SPRINT
            "Ultralong" -> DistanceEnum.ULTRALONG
            "Pre-O" -> DistanceEnum.PREO
            "Temp-O" -> DistanceEnum.TEMPO
            else -> DistanceEnum.MIDDLE
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


    fun convertEvents(eventList: org.iof.eventor.EventList?, eventor: Eventor): List<Event> {
        val result: MutableList<Event> = ArrayList()
        if (eventList != null) {
            for (event in eventList.event) {
                result.add(
                        convertEvent(
                            event = event,
                            eventCLassList = null,
                            documentList = null,
                            organisations = listOf(),
                            regions = listOf(),
                            eventor = eventor
                    )
                )
            }
        }
        return result
    }
    private fun convertRaceDate(time: org.iof.eventor.RaceDate): Timestamp {
        val timeString = time.date.content + "T" + time.clock.content + ".000Z"
        return Timestamp.parseTimestamp(timeString)
    }

    private fun convertStartDate(time: org.iof.eventor.StartDate): Timestamp {
        val timeString = time.date.content + "T" + time.clock.content + ".000Z"
        return Timestamp.parseTimestamp(timeString)
    }


    private fun convertFinishDate(time: org.iof.eventor.FinishDate): Timestamp {
        val timeString = time.date.content + "T" + time.clock.content + ".000Z"
        return Timestamp.parseTimestamp(timeString)
    }

    fun convertPosition(eventCenterPosition: org.iof.eventor.EventCenterPosition): Position {
        return Position(eventCenterPosition.x.toDouble(), eventCenterPosition.y.toDouble())
    }

    private fun convertEntryBreak(entryBreak: org.iof.eventor.EntryBreak): EntryBreak {
        return EntryBreak(
                if (entryBreak.validFromDate != null) convertValidFromDate(entryBreak.validFromDate) else null,
                if (entryBreak.validToDate != null) convertValidToDate(entryBreak.validToDate) else null)
    }

    private fun convertValidFromDate(time: org.iof.eventor.ValidFromDate): Timestamp? {
        val timeString = time.date.content + "T" + time.clock.content + ".000Z"
        return Timestamp.parseTimestamp(timeString)
    }

    private fun convertValidToDate(time: org.iof.eventor.ValidToDate): Timestamp {
        val timeString = time.date.content + "T" + time.clock.content + ".000Z"
        return Timestamp.parseTimestamp(timeString)
    }

    private fun convertHostMessage(hashTableEntries: List<org.iof.eventor.HashTableEntry>): String? {
        for (hashTableEntry in hashTableEntries) {
            if (hashTableEntry.key.content == "Eventor_Message") {
                return hashTableEntry.value.content
            }
        }
        return null
    }


    private fun convertPunchingUnitTypes(punchingUnitTypes: List<org.iof.eventor.PunchingUnitType>): List<String> {
        val result: MutableList<String> = ArrayList()
        for (punchingUnitType in punchingUnitTypes) {
            result.add(punchingUnitType.value)
        }
        return result
    }






    private fun convertEventDocument(documentList: org.iof.eventor.DocumentList?): List<Document> {
        val result: MutableList<Document> = ArrayList()
        if(documentList == null){
            return listOf()
        }
        for (document in documentList.document) {
            result.add(Document(document.name, document.url, document.type))
        }
        return result
    }

    fun convertEventRaceIds(eventRaceIds: List<org.iof.eventor.EventRaceId>): List<String> {
        val result: MutableList<String> = ArrayList()
        for (eventRaceId in eventRaceIds) {
            result.add(eventRaceId.content)
        }
        return result
    }
}