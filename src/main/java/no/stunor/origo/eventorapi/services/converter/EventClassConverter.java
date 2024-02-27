package no.stunor.origo.eventorapi.services.converter;

import java.util.ArrayList;
import java.util.List;

import org.iof.eventor.ClassEntryFee;
import org.iof.eventor.EntryClass;
import org.iof.eventor.EventClassList;
import org.iof.eventor.HashTableEntry;

import no.stunor.origo.eventorapi.model.origo.event.EventClass;
import no.stunor.origo.eventorapi.model.origo.event.EventClassTypeEnum;

public class EventClassConverter {
    public static List<EventClass> convertEventClasses(EventClassList eventCLassList) {
        List<EventClass> result = new ArrayList<>();
        for (org.iof.eventor.EventClass eventClass : eventCLassList.getEventClass()) {
            if(eventClass != null) {
                result.add(convertEventClass(eventClass));
            }

        }
        return result;
    }

    public static EventClass convertEventClass(org.iof.eventor.EventClass eventClass) {
        return new EventClass(
                eventClass.getEventClassId().getContent(),
                eventClass.getName().getContent(),
                eventClass.getClassShortName().getContent(),
                getClasstypeFromId(eventClass.getClassType() != null ? eventClass.getClassType().getClassTypeId().getContent() : eventClass.getClassTypeId().getContent()),
                eventClass.getLowAge() != null ? Integer.parseInt(eventClass.getLowAge()) : null,
                eventClass.getHighAge() != null ? Integer.parseInt(eventClass.getHighAge()) : null,
                eventClass.getSex(),
                getTimePresentation(eventClass.getHashTableEntry()),
                getResultListMode(eventClass.getHashTableEntry()),
                eventClass.getNumberOfLegs() != null ? Integer.parseInt(eventClass.getNumberOfLegs()) : null, 
                eventClass.getMinAverageAge() != null ? Integer.parseInt(eventClass.getMinAverageAge()) : null, 
                eventClass.getMaxAverageAge() != null ? Integer.parseInt(eventClass.getMaxAverageAge()) : null, 
                convertEntryFees(eventClass.getClassEntryFee()));
    }

    private static EventClassTypeEnum getClasstypeFromId(String classTypeId) {
        switch (classTypeId) {
            case "1":
                return EventClassTypeEnum.ELITE;
            case "2":
                return EventClassTypeEnum.NORMAL;
            case "3":
                return EventClassTypeEnum.OPEN;
            default:
                return EventClassTypeEnum.NORMAL;
        }
    }

    private static List<String> convertEntryFees(List<ClassEntryFee> classEntryFees) {
        List<String> result = new ArrayList<>();
        for(ClassEntryFee entryFee : classEntryFees){
            result.add(entryFee.getEntryFeeId().getContent());
        }
        return result;
    }

    private static boolean getResultListMode(List<HashTableEntry> hashTableEntryList) {
        for (HashTableEntry hashTableEntry : hashTableEntryList){
            if(hashTableEntry.getKey().getContent().equals("Eventor_ResultListMode")){
                if(hashTableEntry.getValue().getContent().equals("UnorderedNoTimes") ||
                        hashTableEntry.getValue().getContent().equals("Unordered")){
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean getTimePresentation(List<HashTableEntry> hashTableEntryList) {
        for (HashTableEntry hashTableEntry : hashTableEntryList){
            if(hashTableEntry.getKey().getContent().equals("Eventor_ResultListMode")){
                if(hashTableEntry.getValue().getContent().equals("UnorderedNoTimes")){
                    return false;
                }
            }
        }
        return true;
    }

    public static List<String> convertEventClassIds(List<EntryClass> entryClasses) {
        List<String> eventClassIds = new ArrayList<>();
        for(EntryClass entryClass : entryClasses){
            if(entryClass.getEventClassId()!= null){
                eventClassIds.add(entryClass.getEventClassId().getContent());
            }
        }
        return eventClassIds;
    }

    public static String convertEventClassId(EntryClass entryClass) {
        if(entryClass.getEventClassId()!= null){
            return entryClass.getEventClassId().getContent();
        }
        return null;
    }

    public static EventClass getEventClassFromId(EventClassList eventClassList, String entryClassId) {
        for(org.iof.eventor.EventClass eventClass : eventClassList.getEventClass()){
            if(eventClass.getEventClassId().getContent().equals(entryClassId)){
                return convertEventClass(eventClass);
            }
        }
        return null;
    }



}
