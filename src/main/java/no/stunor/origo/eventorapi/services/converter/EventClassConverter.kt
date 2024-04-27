package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.event.EventClass
import no.stunor.origo.eventorapi.model.event.EventClassTypeEnum
import org.iof.eventor.ClassEntryFee
import org.iof.eventor.EntryClass
import org.iof.eventor.EventClassList
import org.iof.eventor.HashTableEntry
import org.springframework.stereotype.Component

@Component
class EventClassConverter {
    fun convertEventClasses(eventCLassList: EventClassList?): List<EventClass> {
        if(eventCLassList == null)
            return listOf()
        val result: MutableList<EventClass> = ArrayList()
        for (eventClass in eventCLassList.eventClass) {
            if (eventClass != null) {
                result.add(convertEventClass(eventClass))
            }
        }
        return result
    }

    fun convertEventClass(eventClass: org.iof.eventor.EventClass): EventClass {
        return EventClass(
                eventClassId = eventClass.eventClassId.content,
                name = eventClass.name.content,
                shortName = eventClass.classShortName.content,
                type = getClassTypeFromId(if (eventClass.classType != null) eventClass.classType.classTypeId.content else eventClass.classTypeId.content),
                minAge = if (eventClass.lowAge != null) eventClass.lowAge.toInt() else null,
                maxAge = if (eventClass.highAge != null) eventClass.highAge.toInt() else null,
                gender = eventClass.sex,
                presentTime = getTimePresentation(eventClass.hashTableEntry),
                orderedResult = getResultListMode(eventClass.hashTableEntry),
                legs = (if (eventClass.numberOfLegs != null) eventClass.numberOfLegs.toInt() else null)!!,
                minAverageAge = if (eventClass.minAverageAge != null) eventClass.minAverageAge.toInt() else null,
                maxAverageAge =  if (eventClass.maxAverageAge != null) eventClass.maxAverageAge.toInt() else null,
                entryFees = convertEntryFees(eventClass.classEntryFee))
    }

    private fun getClassTypeFromId(classTypeId: String): EventClassTypeEnum {
        return when (classTypeId) {
            "1" -> EventClassTypeEnum.Elite
            "3" -> EventClassTypeEnum.Open
            else -> EventClassTypeEnum.Normal
        }
    }

    private fun convertEntryFees(classEntryFees: List<ClassEntryFee>): List<String> {
        val result: MutableList<String> = ArrayList()
        for (entryFee in classEntryFees) {
            result.add(entryFee.entryFeeId.content)
        }
        return result
    }

    private fun getResultListMode(hashTableEntryList: List<HashTableEntry>): Boolean {
        for (hashTableEntry in hashTableEntryList) {
            if (hashTableEntry.key.content == "Eventor_ResultListMode") {
                if (hashTableEntry.value.content == "UnorderedNoTimes" || hashTableEntry.value.content == "Unordered") {
                    return false
                }
            }
        }
        return true
    }

    private fun getTimePresentation(hashTableEntryList: List<HashTableEntry>): Boolean {
        for (hashTableEntry in hashTableEntryList) {
            if (hashTableEntry.key.content == "Eventor_ResultListMode") {
                if (hashTableEntry.value.content == "UnorderedNoTimes") {
                    return false
                }
            }
        }
        return true
    }

    fun convertEventClassIds(entryClasses: List<EntryClass>): List<String> {
        val eventClassIds: MutableList<String> = ArrayList()
        for (entryClass in entryClasses) {
            if (entryClass.eventClassId != null) {
                eventClassIds.add(entryClass.eventClassId.content)
            }
        }
        return eventClassIds
    }

    fun convertEventClassId(entryClass: EntryClass): String? {
        if (entryClass.eventClassId != null) {
            return entryClass.eventClassId.content
        }
        return null
    }

    fun getEventClassFromId(eventClassList: EventClassList, entryClassId: String): EventClass? {
        for (eventClass in eventClassList.eventClass) {
            if (eventClass.eventClassId.content == entryClassId) {
                return convertEventClass(eventClass)
            }
        }
        return null
    }
}
