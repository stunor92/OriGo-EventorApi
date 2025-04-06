package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.Eventor
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Component
class TimeStampConverter {
    fun parseTimestamp(time: String): Instant {
        val sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return Instant.from(sdf.withZone(ZoneId.systemDefault()).parse(time))
    }
    fun parseTimestamp(time: String, eventor: Eventor): ZonedDateTime {
        val sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return ZonedDateTime.parse(time, sdf.withZone(getTimeZone(eventor)))
    }

    private fun getTimeZone(eventor: Eventor): ZoneId {
        if (eventor.eventorId == "AUS") {
            return ZoneId.of("Australia/Sydney")
        }
        return ZoneId.of("Europe/Paris")
    }
}
