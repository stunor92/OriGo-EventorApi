package no.stunor.origo.eventorapi.model.event.entrylist
import java.io.Serializable


interface CompetitorEntry : Serializable {
    var name: Any
    var raceId: String
    var eventClassId: String
}