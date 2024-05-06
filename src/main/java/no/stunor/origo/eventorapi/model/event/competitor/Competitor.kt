package no.stunor.origo.eventorapi.model.event.competitor

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.cloud.Timestamp
import com.google.cloud.firestore.annotation.DocumentId
import java.io.Serializable


interface Competitor : Serializable {
    var id: String
    var eventorId: String
    var eventId: String
    var raceId: String
    var eventClassId: String
    var bib: String?
    var startTime: Timestamp?
    var finishTime: Timestamp?
}