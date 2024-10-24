package no.stunor.origo.eventorapi.services.converter

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.event.EntryFee
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.Price
import org.iof.eventor.EntryFeeList
import org.springframework.stereotype.Component

@Component
class EntryFeeConverter {

    fun convertEventEntryFees(entryFees: EntryFeeList, event: Event): List<EntryFee> {
        val result: MutableList<EntryFee> = mutableListOf()

        for (entryFee in entryFees.entryFee) {
            result.add(convertEntryFee(entryFee, event))
        }
        return result
    }

    private fun convertEntryFee(entryFee: org.iof.eventor.EntryFee, event: Event): EntryFee {
        return EntryFee(
            entryFeeId = entryFee.entryFeeId.content,
            name = entryFee.name.content,
            price = if(entryFee.valueOperator == "fixed" && entryFee.amount != null) convertPrice(entryFee.amount) else null,
            externalFee = if(entryFee.externalFee != null) convertExternalFee(entryFee.externalFee) else null,
            percentageSurcharge = if(entryFee.valueOperator == "percent" && entryFee.amount != null) entryFee.amount.content.toInt() else null,
            validFrom = if (entryFee.validFromDate != null) convertValidFromDate(entryFee.validFromDate) else null,
            validTo = if (entryFee.validToDate != null) convertValidToDate(entryFee.validToDate) else null,
            fromBirthYear = if(entryFee.fromDateOfBirth != null) entryFee.fromDateOfBirth.date.content.substring(0,4).toInt() else null,
            toBirthYear = if(entryFee.toDateOfBirth != null) entryFee.toDateOfBirth.date.content.substring(0,4).toInt() else null,
            taxIncluded = if (entryFee.taxIncluded == "Y") true else false,
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

    private fun convertValidFromDate(time: org.iof.eventor.ValidFromDate): Timestamp? {
        val timeString = time.date.content + "T" + time.clock.content + ".000Z"
        return Timestamp.parseTimestamp(timeString)
    }

    private fun convertValidToDate(time: org.iof.eventor.ValidToDate): Timestamp {
        val timeString = time.date.content + "T" + time.clock.content + ".000Z"
        return Timestamp.parseTimestamp(timeString)
    }
}

