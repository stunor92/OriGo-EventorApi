package no.stunor.origo.eventorapi.services.converter

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.EntryFee
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.Price
import org.iof.eventor.EntryFeeList
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Component
class EntryFeeConverter {

    fun convertEventEntryFees(entryFees: EntryFeeList, event: Event, eventor: Eventor): List<EntryFee> {
        val result: MutableList<EntryFee> = mutableListOf()

        for (entryFee in entryFees.entryFee) {
            result.add(convertEntryFee(entryFee, event, eventor))
        }
        return result
    }

    private fun convertEntryFee(entryFee: org.iof.eventor.EntryFee, event: Event, eventor: Eventor): EntryFee {
        return EntryFee(
            entryFeeId = entryFee.entryFeeId.content,
            name = entryFee.name.content,
            price = if(entryFee.valueOperator == "fixed" && entryFee.amount != null) convertPrice(entryFee.amount) else null,
            externalFee = if(entryFee.externalFee != null) convertExternalFee(entryFee.externalFee) else null,
            percentageSurcharge = if(entryFee.valueOperator == "percent" && entryFee.amount != null) entryFee.amount.content.toInt() else null,
            validFrom = if (entryFee.validFromDate != null) convertValidFromDate(entryFee.validFromDate, eventor) else null,
            validTo = if (entryFee.validToDate != null) convertValidToDate(entryFee.validToDate, eventor) else null,
            fromBirthYear = if(entryFee.fromDateOfBirth != null) entryFee.fromDateOfBirth.date.content.substring(0,4).toInt() else null,
            toBirthYear = if(entryFee.toDateOfBirth != null) entryFee.toDateOfBirth.date.content.substring(0,4).toInt() else null,
            taxIncluded = entryFee.taxIncluded == "Y",
            eventClasses = getEventClasses(event)
        )

    }

    private fun convertPrice(amount: org.iof.eventor.Amount): Price {
        return Price(
            amount = amount.content.toInt(),
            currency = amount.currency
        )
    }

    private fun convertExternalFee(amount: org.iof.eventor.ExternalFee): Price {
        return Price(
            amount = amount.content.toInt(),
            currency = amount.currency
        )
    }

    private fun getEventClasses(event: Event): List<String> {
        val result: MutableList<String> = mutableListOf()
        for (eventClass in event.eventClasses) {
            result.add(eventClass.eventClassId)
        }
        return result
    }

    private fun convertValidFromDate(time: org.iof.eventor.ValidFromDate, eventor: Eventor): Timestamp {
        val timeString = time.date.content + " " + time.clock.content
        val zdt = parseTimestamp(timeString, eventor)
        return Timestamp.ofTimeSecondsAndNanos(zdt.toInstant().epochSecond, 0)
    }

    private fun convertValidToDate(time: org.iof.eventor.ValidToDate, eventor: Eventor): Timestamp {
        val timeString = time.date.content + " " + time.clock.content
        val zdt = parseTimestamp(timeString, eventor)
        return Timestamp.ofTimeSecondsAndNanos(zdt.toInstant().epochSecond, 0)
    }

    private fun parseTimestamp(time: String, eventor: Eventor): ZonedDateTime {
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

