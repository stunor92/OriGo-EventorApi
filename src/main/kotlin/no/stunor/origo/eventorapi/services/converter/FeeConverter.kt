package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.EventClass
import no.stunor.origo.eventorapi.model.event.Fee

class FeeConverter {
    companion object {
        fun convertEntryFees(
            entryFees: org.iof.eventor.EntryFeeList?,
            event: Event,
            eventClassList: List<org.iof.eventor.EventClass>
        ): MutableList<Fee> {
            val result = mutableListOf<Fee>()

            if (entryFees == null) return result

            for (entryFee in entryFees.entryFee) {
                result.add(convertEntryFee(entryFee, event, eventClassList))
            }
            return result
        }

        private fun convertEntryFee(
            entryFee: org.iof.eventor.EntryFee,
            event: Event,
            eventClassList: List<org.iof.eventor.EventClass>
        ): Fee {
            return Fee(
                eventorId = event.eventor.eventorId,
                eventId = event.eventId,
                feeId = entryFee.entryFeeId.content,
                name = entryFee.name.content,
                currency = if (entryFee.valueOperator == "fixed" && entryFee.amount != null) entryFee.amount.currency else null,
                amount = if (entryFee.valueOperator == "fixed" && entryFee.amount != null) entryFee.amount.content.toDouble() else null,
                externalFee = if (entryFee.externalFee != null) entryFee.externalFee.content.toDouble() else null,
                percentageSurcharge = if (entryFee.valueOperator == "percent" && entryFee.amount != null) entryFee.amount.content.toInt() else null,
                validFrom = if (entryFee.validFromDate != null) TimeStampConverter.parseDate(
                    "${entryFee.validFromDate.date.content} ${entryFee.validFromDate.clock.content}",
                    event.eventor
                ) else null,
                validTo = if (entryFee.validToDate != null) TimeStampConverter.parseDate(
                    "${entryFee.validToDate.date.content} ${entryFee.validToDate.clock.content}",
                    event.eventor
                ) else null,
                fromBirthYear = if (entryFee.fromDateOfBirth != null) entryFee.fromDateOfBirth.date.content.substring(
                    0,
                    4
                ).toInt() else null,
                toBirthYear = if (entryFee.toDateOfBirth != null) entryFee.toDateOfBirth.date.content.substring(0, 4)
                    .toInt() else null,
                taxIncluded = entryFee.taxIncluded == "Y",
                classes = findEventClasses(event.classes, entryFee, eventClassList)
            )
        }

        private fun findEventClasses(
            classes: List<EventClass>,
            fee: org.iof.eventor.EntryFee,
            eventClassList: List<org.iof.eventor.EventClass>
        ): MutableList<EventClass> {
            val result = mutableListOf<EventClass>()

            for (eventClass in eventClassList) {
                for (classFee in eventClass.classEntryFee) {
                    if (classFee.entryFeeId.content == fee.entryFeeId.content) {
                        classes.find{ it.classId == eventClass.eventClassId.content }?.let { result.add(it) }
                    }
                }
            }
            return result
        }
    }
}

