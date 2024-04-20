package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.calendar.CalendarRace
import no.stunor.origo.eventorapi.model.Eventor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.collections.ArrayList

@Component
class CalendarConverter {
    @Autowired
    private lateinit var eventConverter: EventConverter
    fun convertEvents(eventList: org.iof.eventor.EventList?, eventor: Eventor, competitorCountList: org.iof.eventor.CompetitorCountList?): List<CalendarRace> {
        val result: MutableList<CalendarRace> = ArrayList()
        for (event in eventList!!.event) {
            result.addAll(convertEvent(event, eventor, competitorCountList))
        }
        return result
    }

    private fun convertEvent(event: org.iof.eventor.Event, eventor: Eventor, competitorCountList: org.iof.eventor.CompetitorCountList?): List<CalendarRace> {
        val result: MutableList<CalendarRace> = ArrayList()
        for (eventRace in event.eventRace) {
            result.add(convertRace(event, eventRace, eventor, competitorCountList))
        }
        return result
    }

    private fun convertRace(event: org.iof.eventor.Event, eventRace: org.iof.eventor.EventRace, eventor: Eventor, competitorCountList: org.iof.eventor.CompetitorCountList?): CalendarRace {
        return CalendarRace(
                eventor,
                event.eventId.content,
                eventRace.eventRaceId.content,
                event.name.content,
                eventRace.name.content,
                (if (eventRace.raceDate != null) eventConverter.convertRaceDateWithoutTime(eventRace.raceDate) else null)!!,
                eventConverter.convertEventForm(event.eventForm),
                eventConverter.convertEventClassification(event.eventClassificationId.content),
                eventConverter.convertLightCondition(eventRace.raceLightCondition),
                eventConverter.convertRaceDistance(eventRace.raceDistance),
                if (eventRace.eventCenterPosition != null) eventConverter.convertPosition(eventRace.eventCenterPosition) else null,
                eventConverter.convertEventStatus(event.eventStatusId.content),
                eventConverter.convertEventDisciplines(event.disciplineIdOrDiscipline),
                eventConverter.convertOrganisers(eventor, event.organiser.organisationIdOrOrganisation),
                eventConverter.convertEntryBreaks(event.entryBreak),
                isSignedUp(event.eventId.content, competitorCountList),
                getEntries(event.eventId.content, eventRace.eventRaceId.content, competitorCountList),
                getOrganisationEntries(event.eventId.content, eventRace.eventRaceId.content, competitorCountList),
                eventConverter.hasStartList(event.hashTableEntry, eventRace.eventRaceId.content),
                eventConverter.hasResultList(event.hashTableEntry, eventRace.eventRaceId.content),
                eventConverter.hasLivelox(event.hashTableEntry))
    }

    private fun isSignedUp(eventId: String, competitorCountList: org.iof.eventor.CompetitorCountList?): Boolean {
        for (competitorCount in competitorCountList!!.competitorCount) {
            if (competitorCount.eventId == eventId && competitorCount.classCompetitorCount != null && competitorCount.classCompetitorCount.isNotEmpty()) {
                return true
            }
        }
        return false
    }

    private fun getEntries(eventId: String, eventRaceId: String, competitorCountList: org.iof.eventor.CompetitorCountList?): Int {
        for (competitorCount in competitorCountList!!.competitorCount) {
            if (competitorCount.eventId == eventId) {
                if (competitorCount.eventRaceId == null) {
                    return competitorCount.numberOfEntries.toInt()
                } else if (competitorCount.eventRaceId == eventRaceId) {
                    return competitorCount.numberOfEntries.toInt()
                }
            }
        }
        return 0
    }

    private fun getOrganisationEntries(eventId: String, eventRaceId: String, competitorCountList: org.iof.eventor.CompetitorCountList?): Map<String, Int> {
        val result: MutableMap<String, Int> = HashMap()

        for (competitorCount in competitorCountList!!.competitorCount) {
            if (competitorCount.eventId == eventId) {
                if (competitorCount.eventRaceId == null) {
                    if (competitorCount.organisationCompetitorCount != null) {
                        for (organisationCompetitorCount in competitorCount.organisationCompetitorCount) {
                            result[organisationCompetitorCount.organisationId] = organisationCompetitorCount.numberOfEntries.toInt()
                        }
                    }
                } else if (competitorCount.eventRaceId == eventRaceId) {
                    if (competitorCount.organisationCompetitorCount != null) {
                        for (organisationCompetitorCount in competitorCount.organisationCompetitorCount) {
                            result[organisationCompetitorCount.organisationId] = organisationCompetitorCount.numberOfEntries.toInt()
                        }
                    }
                }
            }
        }
        return result
    }
}
