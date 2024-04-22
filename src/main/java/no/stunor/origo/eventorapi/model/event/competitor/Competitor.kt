package no.stunor.origo.eventorapi.model.event.competitor

import com.google.cloud.Timestamp
import java.io.Serializable


interface Competitor : Serializable {
    var eventorId: String
    var eventId: String
    var raceId: String
    var eventClassId: String
    var bib: String?
    var startTime: Timestamp?
    var finishTime: Timestamp?
    var entryFeeIds: List<String>
}