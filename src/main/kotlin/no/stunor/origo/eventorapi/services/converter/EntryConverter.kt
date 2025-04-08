package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.EntryFee
import no.stunor.origo.eventorapi.model.event.Price
import org.iof.eventor.EntryFeeList
import org.iof.eventor.EventClassList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.Instant

@Component
class EntryConverter {

    @Autowired
    private lateinit var timeStampConverter: TimeStampConverter
    fun convertEntryFeesIds(entryFees: List<org.iof.eventor.EntryEntryFee>?, raceId: String?): List<String> {
        val result  = mutableListOf<String>()
        for (entryFee in entryFees?: emptyList()) {
            if (raceId == null || entryFee.eventRaceId == raceId)
                result.add(entryFee.entryFeeId.content)
        }
        return result
    }

    fun convertEntryFees(entryFees: EntryFeeList, eventor: Eventor, eventClasses: EventClassList): List<EntryFee> {
        val result  = mutableListOf<EntryFee>()

        for (entryFee in entryFees.entryFee) {
            result.add(convertEntryFee(entryFee, eventor, eventClasses))
        }
        return result
    }

    private fun convertEntryFee(entryFee: org.iof.eventor.EntryFee, eventor: Eventor, eventClasses: EventClassList): EntryFee {
        return EntryFee(
            entryFeeId = entryFee.entryFeeId.content,
            name = entryFee.name.content,
            price = if(entryFee.valueOperator == "fixed" && entryFee.amount != null) convertPrice(entryFee.amount) else null,
            externalFee = if(entryFee.externalFee != null) convertExternalFee(entryFee.externalFee) else null,
            percentageSurcharge = if(entryFee.valueOperator == "percent" && entryFee.amount != null) entryFee.amount.content.toInt() else null,
            validFrom = if (entryFee.validFromDate != null) timeStampConverter.parseTimestamp("${entryFee.validFromDate.date.content} ${entryFee.validFromDate.clock.content}", eventor) else null,
            validTo = if (entryFee.validToDate != null) timeStampConverter.parseTimestamp("${entryFee.validToDate.date.content} ${entryFee.validToDate.clock.content}", eventor) else null,
            fromBirthYear = if(entryFee.fromDateOfBirth != null) entryFee.fromDateOfBirth.date.content.substring(0,4).toInt() else null,
            toBirthYear = if(entryFee.toDateOfBirth != null) entryFee.toDateOfBirth.date.content.substring(0,4).toInt() else null,
            taxIncluded = entryFee.taxIncluded == "Y",
            eventClasses = findEventClasses(eventClasses, entryFee.entryFeeId.content)
        )

    }

    private fun findEventClasses(eventClasses: EventClassList, entryFeeId: String): List<String> {
        val result  = mutableListOf<String>()
        for(eventClass in eventClasses.eventClass) {
            for (entryFee in eventClass.classEntryFee) {
                if (entryFee.entryFeeId.content == entryFeeId) {
                    result.add(eventClass.eventClassId.content)
                }
            }
        }
        return result
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
    fun convertEntryBreaks(
        entryBreaks: List<org.iof.eventor.EntryBreak>,
        eventor: Eventor
    ): List<Timestamp> {
        val result: MutableList<Timestamp> = ArrayList()
        for (entryBreak in entryBreaks) {
            if (entryBreak.validToDate != null) {
                result.add(
                    timeStampConverter.parseTimestamp(
                        "${entryBreak.validToDate.date.content} ${entryBreak.validToDate.clock.content}",
                        eventor
                    )
                )
            }
        }
        return result
    }
}

