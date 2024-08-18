package no.stunor.origo.eventorapi.model.event.competitor.eventor

import com.google.cloud.Timestamp
import java.io.Serializable


interface EventorCompetitor : Serializable {
    var name: Any
    var raceId: String
    var eventClassId: String
    var bib: String?
    var startTime: Timestamp?
    var finishTime: Timestamp?
}