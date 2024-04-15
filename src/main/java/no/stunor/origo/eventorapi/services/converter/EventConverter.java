package no.stunor.origo.eventorapi.services.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.iof.eventor.DisciplineId;
import org.iof.eventor.DocumentList;
import org.iof.eventor.EventCenterPosition;
import org.iof.eventor.EventClassList;
import org.iof.eventor.EventRace;
import org.iof.eventor.EventRaceId;
import org.iof.eventor.FinishDate;
import org.iof.eventor.HashTableEntry;
import org.iof.eventor.OrganisationId;
import org.iof.eventor.PunchingUnitType;
import org.iof.eventor.RaceDate;
import org.iof.eventor.StartDate;
import org.iof.eventor.ValidFromDate;
import org.iof.eventor.ValidToDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.stunor.origo.eventorapi.data.OrganisationRepository;
import no.stunor.origo.eventorapi.model.firestore.Eventor;
import no.stunor.origo.eventorapi.model.firestore.Organisation;
import no.stunor.origo.eventorapi.model.firestore.Region;
import no.stunor.origo.eventorapi.model.origo.common.CCard;
import no.stunor.origo.eventorapi.model.origo.common.Position;
import no.stunor.origo.eventorapi.model.origo.entry.EntryBreak;
import no.stunor.origo.eventorapi.model.origo.event.DisiplineEnum;
import no.stunor.origo.eventorapi.model.origo.event.DistanceEnum;
import no.stunor.origo.eventorapi.model.origo.event.Document;
import no.stunor.origo.eventorapi.model.origo.event.Event;
import no.stunor.origo.eventorapi.model.origo.event.EventClassificationEnum;
import no.stunor.origo.eventorapi.model.origo.event.EventFormEnum;
import no.stunor.origo.eventorapi.model.origo.event.EventStatusEnum;
import no.stunor.origo.eventorapi.model.origo.event.LightConditionEnum;
import no.stunor.origo.eventorapi.model.origo.event.Race;
import no.stunor.origo.eventorapi.model.origo.user.UserCompetitor;
import no.stunor.origo.eventorapi.model.origo.user.UserRace;

@Component
public class EventConverter {

    @Autowired
    OrganisationRepository organisationRepository;

    public EventClassificationEnum convertEventClassification(String eventForm) {
        return switch (eventForm) {
            case "1" -> EventClassificationEnum.CHAMPIONSHIP;
            case "2" -> EventClassificationEnum.NATIONAL;
            case "3" -> EventClassificationEnum.REGIONAL;
            case "4" -> EventClassificationEnum.LOCAL;
            default -> EventClassificationEnum.CLUB;
        };
    }


    public EventStatusEnum convertEventStatus(String eventStatusId) {
        return switch (eventStatusId) {
            case "2"    -> EventStatusEnum.REGIONAPPROVED;
            case "3"    -> EventStatusEnum.APPROVED;
            case "4"    -> EventStatusEnum.CREATED;
            case "5"    -> EventStatusEnum.ENTRYOPEN;
            case "6"    -> EventStatusEnum.ENTRYPAUSED;
            case "7"    -> EventStatusEnum.ENTRYCLOSED;
            case "8"    -> EventStatusEnum.LIVE;
            case "9"    -> EventStatusEnum.COMPLETED;
            case "10"   -> EventStatusEnum.CANCELED;
            case "11"   -> EventStatusEnum.REPORTED;
            default     -> EventStatusEnum.APPLIED;
        };
    }
    public static Date convertRaceDate(RaceDate startTime) {
        String dateString = startTime.getDate().getContent() + " " + startTime.getClock().getContent();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return parser.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public Date convertRaceDateWhitoutTime(RaceDate startTime) {
        String dateString = startTime.getDate().getContent();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return parser.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date convertStartDate(StartDate startTime) {
        String dateString = startTime.getDate().getContent() + " " + startTime.getClock().getContent();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return parser.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
    

    public static Date convertFinishDate(FinishDate startTime) {
        String dateString = startTime.getDate().getContent() + " " + startTime.getClock().getContent();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return parser.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
    public static Position convertPosition(EventCenterPosition eventCenterPosition) {
        return new Position(Double.parseDouble(eventCenterPosition.getX()),Double.parseDouble(eventCenterPosition.getY()));
    }

     public List<Organisation> convertOrganisers(Eventor eventor, List<Object> organisers) {
        List<Organisation> result = new ArrayList<>();
        for (Object organiser: organisers){
            Organisation o = organisationRepository.findByOrganisationIdAndEventorId(((OrganisationId) organiser).getContent(), eventor.getEventorId()).block();
            if(o != null) result.add(o);   
        }
        return result;
    }

    public List<DisiplineEnum> convertEventDisciplines(List<Object> disciplineIds) {
        List<DisiplineEnum> result = new ArrayList<>();
        for (Object disciplineId: disciplineIds){
            result.add(convertEventDiscipline(disciplineId));
        }
        return result;
    }

       public EventFormEnum convertEventForm(String eventForm) {
        return switch (eventForm) {
            case "IndSingleDay", "IndMultiDay"-> EventFormEnum.INDIVIDUAL;
            case "RelaySingleDay", "RelayMultiDay" -> EventFormEnum.RELAY;
            case "PatrolSingleDay", "PatrolMultiDay" -> EventFormEnum.PATROL;
            case "TeamSingleDay", "TeamMultiDay" -> EventFormEnum.TEAM;
            default -> EventFormEnum.INDIVIDUAL;
        };
    }

    public DisiplineEnum convertEventDiscipline(Object disciplineId) {
        return switch (((DisciplineId) disciplineId).getContent()) {
            case "1" -> DisiplineEnum.FOOT;
            case "2" -> DisiplineEnum.MTB;
            case "3" -> DisiplineEnum.SKI;
            case "4" -> DisiplineEnum.PRE;
            default -> DisiplineEnum.FOOT;
        };
    }

    public List<EntryBreak> convertEntryBreaks(List<org.iof.eventor.EntryBreak> entryBreaks) {
        List<EntryBreak> result = new ArrayList<>();
        for(org.iof.eventor.EntryBreak entryBreak : entryBreaks){
            result.add(convertEntryBreak(entryBreak));
        }
        return result;
    }

    private static EntryBreak convertEntryBreak(org.iof.eventor.EntryBreak entryBreak) {
        return new EntryBreak(
                entryBreak.getValidFromDate() != null ? convertValidFromDate(entryBreak.getValidFromDate()) : null,
                entryBreak.getValidToDate() != null ? convertValidToDate(entryBreak.getValidToDate()) : null);
    }

    public static Date convertValidFromDate(ValidFromDate timeDate) {
        String dateString = timeDate.getDate().getContent() + " " + timeDate.getClock().getContent();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return parser.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
    public static Date convertValidToDate(ValidToDate timeDate) {
        String dateString = timeDate.getDate().getContent() + " " + timeDate.getClock().getContent();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return parser.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
    
    public Event convertEvent(
        org.iof.eventor.Event event, 
        EventClassList eventCLassList, 
        DocumentList documentList,
        List<Organisation> organisations,
        List<Region> regions,
        Eventor eventor,
        List<UserRace> raceCompetitors) {
        return new Event(
                eventor,
                event.getEventId().getContent(),
                event.getName().getContent(),
                convertEventForm(event.getEventForm()),
                convertEventClassification(event.getEventClassificationId().getContent()),
                convertEventStatus(event.getEventStatusId().getContent()),
                convertEventDisciplines(event.getDisciplineIdOrDiscipline()),
                event.getStartDate() != null ? convertStartDate(event.getStartDate()) : null,
                event.getFinishDate() != null ? convertFinishDate(event.getFinishDate()) : null,
                organisations,
                regions,
                EventClassConverter.convertEventClasses(eventCLassList),
                convertEventDocument(documentList),
                convertEntryBreaks(event.getEntryBreak()),
                convertRaces(event, event.getEventRace(), raceCompetitors),
                convertPunchingUnitTypes(event.getPunchingUnitType()),
                null, //event.getWebURL(),
                convertHostMessage(event.getHashTableEntry()),
                null,
                null);
                //event.getContactData() != null ? CommonConverter.convertEmail(event.getContactData().getTele()) : null,
                //event.getContactData() != null ? CommonConverter.convertPhone(event.getContactData().getTele()) : null);
    }

    private List<Race> convertRaces(org.iof.eventor.Event event, List<EventRace> eventRaces, List<UserRace> raceCompetitors) {
        List<Race> result = new ArrayList<>();
       
        for(EventRace eventRace : eventRaces){
            List<UserCompetitor> userCompetitors = new ArrayList<>();

            for(UserRace r : raceCompetitors){
                if(r.getRaceId().equals(eventRace.getEventRaceId().getContent())){
                    userCompetitors.addAll(r.getUserCompetitors());
                }
            }
            result.add(convertRace(event, eventRace, userCompetitors));
        }
        return result;
    }

    private Race convertRace(org.iof.eventor.Event event, EventRace eventRace, List<UserCompetitor> competitors) {
        return new Race(
            eventRace.getEventRaceId().getContent(),
            eventRace.getName().getContent(),
            convertLightCondition(eventRace.getRaceLightCondition()),
            convertRaceDistance(eventRace.getRaceDistance()),
            eventRace.getRaceDate() != null ? convertRaceDate(eventRace.getRaceDate()) : null,
            eventRace.getEventCenterPosition() != null ? convertPosition(eventRace.getEventCenterPosition()) : null,
            hasStartList(event.getHashTableEntry(), eventRace.getEventRaceId().getContent()),
            hasResultList(event.getHashTableEntry(), eventRace.getEventRaceId().getContent()),
            hasLivelox(event.getHashTableEntry()),
            competitors
        );
    }

   

    public LightConditionEnum convertLightCondition(String lightCondition) {
         return switch (lightCondition) {
            case "Day" -> LightConditionEnum.DAY;
            case "Night" -> LightConditionEnum.NIGHT;
            case "DayAndNight" -> LightConditionEnum.COMBINED;

            default -> LightConditionEnum.DAY;
        };
    }


    public DistanceEnum convertRaceDistance(String raceDistance) {
        if(raceDistance == null){
            return DistanceEnum.MIDDLE;
        }
        return switch (raceDistance) {
            case "Long" -> DistanceEnum.LONG;
            case "Middle" -> DistanceEnum.MIDDLE;
            case "Sprint", "SprintRelay" -> DistanceEnum.SPRINT;
            case "Ultralong" -> DistanceEnum.ULTRALONG;
            case "Pre-O" -> DistanceEnum.PREO;
            case "Temp-O" -> DistanceEnum.TEMPO;
            default -> DistanceEnum.MIDDLE;
        };
    }


    private static String convertHostMessage(List<HashTableEntry> hashTableEntries) {
        for (HashTableEntry hashTableEntry : hashTableEntries){
            if(hashTableEntry.getKey().getContent().equals("Eventor_Message")){
                return hashTableEntry.getValue().getContent();
            }
        }
        return null;
    }


    public static List<String> convertPunchingUnitTypes(List<PunchingUnitType> punchingUnitTypes) {
        List<String> result = new ArrayList<>();
        for(PunchingUnitType punchingUnitType : punchingUnitTypes){
            result.add(punchingUnitType.getValue());
        }
        return result;
    }

    public static List<CCard> convertCCards(List<org.iof.eventor.CCard> cCards) {
        List<CCard> result = new ArrayList<>();
        for(org.iof.eventor.CCard cCard : cCards){
            result.add(convertCCard(cCard));
        }
        return result;
    }

    public static CCard convertCCard(org.iof.eventor.CCard cCard) {
        return new CCard(cCard.getCCardId().getContent(), cCard.getPunchingUnitType().getValue());
    }
   

    public static List<Document> convertEventDocument(DocumentList documentList) {
        List<Document> result = new ArrayList<>();
        for(org.iof.eventor.Document document :documentList.getDocument()){
            result.add(new Document(document.getName(), document.getUrl(), document.getType()));
        }
        return result;
    }

    public boolean hasStartList(List<HashTableEntry> hashTableEntries, String eventRaceId) {
        for (HashTableEntry hashTableEntry : hashTableEntries){
            if(hashTableEntry.getKey().getContent().equals("startList_"+eventRaceId)){
                return true;
            }
        }
        return false;
    }

    public boolean hasResultList(List<HashTableEntry> hashTableEntries, String eventRaceId) {
        for (HashTableEntry hashTableEntry : hashTableEntries){
            if(hashTableEntry.getKey().getContent().equals("officialResult_"+eventRaceId)){
                return true;
            }
        }
        return false;
    }

    public boolean hasLivelox(List<HashTableEntry> hashTableEntries) {
        for (HashTableEntry hashTableEntry : hashTableEntries){
            if(hashTableEntry.getKey().getContent().equals("Eventor_LiveloxEventConfigurations")){
                return true;
            }
        }
        return false;
    }


    public static List<String> convertEventRaceIds(List<EventRaceId> eventRaceIds) {
        List<String> result = new ArrayList<>();
        for(EventRaceId eventRaceId : eventRaceIds){
            result.add(eventRaceId.getContent());
        }
        return result;
    }

}
