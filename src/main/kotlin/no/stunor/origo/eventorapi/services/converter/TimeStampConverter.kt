package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.Eventor
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Component
class TimeStampConverter {
    fun parseDate(time: String): Timestamp {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return Timestamp.from(Instant.from(formatter.withZone(ZoneOffset.UTC).parse(time)))
    }
    fun parseDate(time: String, eventor: Eventor): Timestamp {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return Timestamp.from(Instant.from(formatter.withZone(getTimeZone(eventor)).parse(time)))
    }

    private fun getTimeZone(eventor: Eventor): ZoneId {
        if (eventor.eventorId == "AUS") {
            return ZoneId.of("Australia/Sydney")
        }
        return ZoneId.of("Europe/Paris")
    }
}
