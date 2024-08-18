package no.stunor.origo.eventorapi.model.event.startlist

import com.google.cloud.Timestamp
import java.io.Serializable


interface CompetitorStart : Serializable {
    var name: Any
    var raceId: String
    var eventClassId: String
    var bib: String?
    var startTime: Timestamp?
}