package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.EventClass
import no.stunor.origo.eventorapi.model.event.Fee
import org.iof.eventor.EntryFeeList
import org.iof.eventor.EventClassList
import org.springframework.stereotype.Component
import java.sql.Timestamp

@Component
class FeeConverter {

    var timeStampConverter =  TimeStampConverter()
    fun convertEntryFeesIds(entryFees: List<org.iof.eventor.EntryEntryFee>?, raceId: String?): List<String> {
        val result  = mutableListOf<String>()
        for (entryFee in entryFees?: emptyList()) {
            if (raceId == null || entryFee.eventRaceId == raceId)
                result.add(entryFee.entryFeeId.content)
        }
        return result
    }

    fun convertEntryFees(entryFees: EntryFeeList?, eventor: Eventor, event: Event, eventClasses: EventClassList?): MutableList<Fee> {
        val result  = mutableListOf<Fee>()

        if (entryFees == null) return result

        for (entryFee in entryFees.entryFee) {
            result.add(convertEntryFee(entryFee, eventor, event, eventClasses))
        }
        return result
    }

    private fun convertEntryFee(entryFee: org.iof.eventor.EntryFee, eventor: Eventor, event: Event, eventClasses: EventClassList?): Fee {
        return Fee(
            eventorId = eventor.eventorId,
            eventId = event.eventId,
            feeId = entryFee.entryFeeId.content,
            name = entryFee.name.content,
            currency = if(entryFee.valueOperator == "fixed" && entryFee.amount != null) entryFee.amount.currency else null,
            amount = if(entryFee.valueOperator == "fixed" && entryFee.amount != null) entryFee.amount.content.toDouble() else null,
            externalFee = if(entryFee.externalFee != null) entryFee.externalFee.content.toDouble() else null,
            percentageSurcharge = if(entryFee.valueOperator == "percent" && entryFee.amount != null) entryFee.amount.content.toInt() else null,
            validFrom = if (entryFee.validFromDate != null) timeStampConverter.parseDate("${entryFee.validFromDate.date.content} ${entryFee.validFromDate.clock.content}", eventor) else null,
            validTo = if (entryFee.validToDate != null) timeStampConverter.parseDate("${entryFee.validToDate.date.content} ${entryFee.validToDate.clock.content}", eventor) else null,
            fromBirthYear = if(entryFee.fromDateOfBirth != null) entryFee.fromDateOfBirth.date.content.substring(0,4).toInt() else null,
            toBirthYear = if(entryFee.toDateOfBirth != null) entryFee.toDateOfBirth.date.content.substring(0,4).toInt() else null,
            taxIncluded = entryFee.taxIncluded == "Y",
            //classes = findEventClasses(eventClasses, entryFee.entryFeeId.content, event, eventor),
        )
    }

    private fun findEventClasses(eventClasses: EventClassList?, entryFeeId: String, event: Event, eventor: Eventor): List<EventClass> {
        val result  = mutableListOf<EventClass>()
        if (eventClasses == null) return result
        for(eventClass in eventClasses.eventClass) {
            for (entryFee in eventClass.classEntryFee) {
                if (entryFee.entryFeeId.content == entryFeeId) {
                    result.add(EventClassConverter.convertEventClass(eventClass,eventor, event))
                }
            }
        }
        return result
    }

    fun convertEntryBreaks(
        entryBreaks: List<org.iof.eventor.EntryBreak>,
        eventor: Eventor
    ): List<Timestamp> {
        val result: MutableList<Timestamp> = ArrayList()
        for (entryBreak in entryBreaks) {
            if (entryBreak.validToDate != null) {
                result.add(
                    timeStampConverter.parseDate(
                        "${entryBreak.validToDate.date.content} ${entryBreak.validToDate.clock.content}",
                        eventor
                    )
                )
            }
        }
        return result
    }
}

