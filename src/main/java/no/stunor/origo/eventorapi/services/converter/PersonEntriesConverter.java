package no.stunor.origo.eventorapi.services.converter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iof.eventor.ClassResult;
import org.iof.eventor.ClassStart;
import org.iof.eventor.Entry;
import org.iof.eventor.EntryList;
import org.iof.eventor.Event;
import org.iof.eventor.EventClassList;
import org.iof.eventor.EventRace;
import org.iof.eventor.EventRaceId;
import org.iof.eventor.PersonResult;
import org.iof.eventor.PersonStart;
import org.iof.eventor.Result;
import org.iof.eventor.ResultList;
import org.iof.eventor.ResultListList;
import org.iof.eventor.Start;
import org.iof.eventor.StartList;
import org.iof.eventor.StartListList;
import org.iof.eventor.TeamResult;
import org.iof.eventor.TeamStart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.stunor.origo.eventorapi.model.firestore.Eventor;
import no.stunor.origo.eventorapi.model.firestore.Person;
import no.stunor.origo.eventorapi.model.origo.user.UserPersonResult;
import no.stunor.origo.eventorapi.model.origo.user.UserPersonStart;
import no.stunor.origo.eventorapi.model.origo.user.UserRace;
import no.stunor.origo.eventorapi.model.origo.user.UserTeamResult;
import no.stunor.origo.eventorapi.model.origo.user.UserTeamStart;
import no.stunor.origo.eventorapi.model.origo.user.UserCompetitor;
import no.stunor.origo.eventorapi.model.origo.user.UserEntry;

@Component
public class PersonEntriesConverter {

    @Autowired
    EventConverter eventConverter;
    
    public List<UserRace> convertPersonEntries(Eventor eventor, Person person, EntryList entryList, StartListList startListList, ResultListList resultListList, Map<String, EventClassList> eventClassMap) throws NumberFormatException, ParseException {
        Map<String, UserRace> result = convertEntryList(eventor, entryList, person, eventClassMap);
        result = convertStartListList(eventor, startListList, person, result);
        result  = convertResultList(eventor, resultListList, person, result);
        return result.values().stream().toList();
    }


    private Map<String, UserRace> convertEntryList(Eventor eventor, EntryList entryList, Person person, Map<String, EventClassList> eventClassMap) throws NumberFormatException, ParseException {
        Map<String, UserRace> raceMap = new HashMap<>();

        for(Entry entry : entryList.getEntry()) {
            for (EventRaceId eventRaceId : entry.getEventRaceId()) {
                for (EventRace race : entry.getEvent().getEventRace()) {
                    if (race.getEventRaceId().getContent().equals(eventRaceId.getContent())) {
                        String raceId = eventRaceId.getContent();
                        if (!raceMap.containsKey(raceId)) {
                            raceMap.put(raceId, createUserRace(eventor, entry.getEvent(), race));
                        }
                        
                        if(!raceMap.get(raceId).getOrganisationEntries().containsKey(entry.getCompetitor().getOrganisationId().getContent())){
                            raceMap.get(raceId).getOrganisationEntries().put(entry.getCompetitor().getOrganisationId().getContent(), 1);
                        } else{
                            int count = raceMap.get(raceId).getOrganisationEntries().get(entry.getCompetitor().getOrganisationId().getContent());
                            raceMap.get(raceId).getOrganisationEntries().put(entry.getCompetitor().getOrganisationId().getContent(), count+1);
                        }

                        if(entry.getCompetitor().getPersonId().getContent().equals(person.getPersonId())){
                            raceMap.get(raceId).getUserCompetitors().add(createUserCompetitor(person, entry, null, null, null, null, eventClassMap.get(raceId)));
                        }
                    }
                }
            }  
        }
        return raceMap;
    }

    private Map<String, UserRace> convertStartListList(Eventor eventor, StartListList startListList,  Person person, Map<String, UserRace> raceMap) throws NumberFormatException, ParseException  {
        for(StartList startList : startListList.getStartList()){
            if(startList.getEvent().getEventRace().size() == 1){
                EventRace race = startList.getEvent().getEventRace().get(0);
                String raceId = race.getEventRaceId().getContent();

                if(!raceMap.containsKey(raceId)){
                    raceMap.put(raceId, createUserRace(eventor, startList.getEvent(), race));
                }

                for(ClassStart classStart : startList.getClassStart()){
                    for(Object start : classStart.getPersonStartOrTeamStart()){
                        if(raceMap.get(raceId).getUserCompetitors().isEmpty()){
                            raceMap.get(raceId).getUserCompetitors().add(createUserCompetitor(person, null, classStart, start, null, null, null));
                        } else {
                            UserEntry userEntry = raceMap.get(raceId).getUserCompetitors().get(0).personEntry();
                            raceMap.get(raceId).getUserCompetitors().remove(0);
                            raceMap.get(raceId).getUserCompetitors().add(updateUserStart(person, userEntry, classStart, start));
                        }
                    }
                }
            } else {
                for(ClassStart classStart : startList.getClassStart()){
                    for(Object start : classStart.getPersonStartOrTeamStart()){
                        if (start instanceof PersonStart){
                            PersonStart personStart = (PersonStart) start;
                            String raceId = personStart.getRaceStart().get(0).getEventRaceId().getContent();
                            for (EventRace race : startList.getEvent().getEventRace()){
                                if(race.getEventRaceId().getContent().equals(raceId)){
                                    if(!raceMap.containsKey(raceId)){
                                        raceMap.put(raceId, createUserRace(eventor, startList.getEvent(), race));
                                    }
                                     if(raceMap.get(raceId).getUserCompetitors().isEmpty()){
                                        raceMap.get(raceId).getUserCompetitors().add(createUserCompetitor(person, null, classStart, start, null, null, null));
                                    } else {
                                        UserEntry userEntry = raceMap.get(raceId).getUserCompetitors().get(0).personEntry();
                                        raceMap.get(raceId).getUserCompetitors().remove(0);
                                        raceMap.get(raceId).getUserCompetitors().add(updateUserStart(person, userEntry, classStart, start));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return raceMap;
    }

    private Map<String, UserRace> convertResultList(Eventor eventor, ResultListList resultListList,  Person person, Map<String, UserRace> raceMap) throws NumberFormatException, ParseException  {
        for(ResultList resultList : resultListList.getResultList()){
            if(resultList.getEvent().getEventRace().size() == 1){
                EventRace race = resultList.getEvent().getEventRace().get(0);
                String raceId = race.getEventRaceId().getContent();

                if(!raceMap.containsKey(raceId)){
                    raceMap.put(raceId, createUserRace(eventor, resultList.getEvent(), race));
                }

                for(ClassResult classResult : resultList.getClassResult()){
                    for(Object result : classResult.getPersonResultOrTeamResult()){
                        if(raceMap.get(raceId).getUserCompetitors().isEmpty()){
                            raceMap.get(raceId).getUserCompetitors().add(createUserCompetitor(person, null, null, null, classResult, result, null));
                        } else {
                            UserEntry userEntry = raceMap.get(raceId).getUserCompetitors().get(0).personEntry();
                            UserPersonStart personStart = raceMap.get(raceId).getUserCompetitors().get(0).personStart();
                            UserTeamStart teamStart = raceMap.get(raceId).getUserCompetitors().get(0).teamStart();

                            raceMap.get(raceId).getUserCompetitors().remove(0);
                            raceMap.get(raceId).getUserCompetitors().add(updateUserResult(person, userEntry, personStart, teamStart, classResult, result));
                        }
                    }
                }
            } else {
                for(ClassResult classResult : resultList.getClassResult()){
                    for(Object result : classResult.getPersonResultOrTeamResult()){
                        if (result instanceof PersonResult){
                            PersonResult personResult = (PersonResult) result;
                            String raceId = personResult.getRaceResult().get(0).getEventRaceId().getContent();
                            for (EventRace race : resultList.getEvent().getEventRace()){
                                if(race.getEventRaceId().getContent().equals(raceId)){
                                    if(!raceMap.containsKey(raceId)){
                                        raceMap.put(raceId, createUserRace(eventor, resultList.getEvent(), race));
                                    }
                                     if(raceMap.get(raceId).getUserCompetitors().isEmpty()){
                                        raceMap.get(raceId).getUserCompetitors().add(createUserCompetitor(person, null, null, null, classResult, result, null));
                                    } else {
                                        UserEntry userEntry = raceMap.get(raceId).getUserCompetitors().get(0).personEntry();
                                        UserPersonStart personStart = raceMap.get(raceId).getUserCompetitors().get(0).personStart();
                                        UserTeamStart teamStart = raceMap.get(raceId).getUserCompetitors().get(0).teamStart();
                                        raceMap.get(raceId).getUserCompetitors().remove(0);
                                        raceMap.get(raceId).getUserCompetitors().add(updateUserResult(person, userEntry, personStart, teamStart, classResult, result));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return raceMap;
    }
    private UserRace createUserRace(Eventor eventor, Event event, EventRace race){
        return new UserRace(
            eventor,
            event.getEventId().getContent(),
            event.getName().getContent(),
            race.getEventRaceId().getContent(),
            race.getName().getContent(),
            event.getEventStatusId().getContent().equals("10"),
            eventConverter.convertRaceDateWhitoutTime(race.getRaceDate()),
            new ArrayList<>(),
            new HashMap<>(),
            eventConverter.convertEntryBreaks(event.getEntryBreak()));
    }

    private static UserCompetitor createUserCompetitor(Person person, Entry entry, ClassStart classStart, Object start, ClassResult classResult, Object result, EventClassList eventClassList) throws NumberFormatException, ParseException {
        return new UserCompetitor(
            person.getPersonId(),
            person.getName(),
            entry != null ? createUserEntry(entry, eventClassList) : null,
            start != null && start instanceof PersonStart ? createPersonStart((PersonStart) start, classStart) : null,
            start != null && start instanceof TeamStart ? createTeamStart((TeamStart) start, classStart) : null,
            result != null && result instanceof PersonResult ? createPersonResult((PersonResult) result, classResult) : null,
            result != null && result instanceof TeamResult ? createTeamResult((TeamResult) result, classResult) : null
        );
    }

    private static UserCompetitor updateUserStart(Person person, UserEntry userEntry, ClassStart classStart, Object start){
        return new UserCompetitor(
            person.getPersonId(),
            person.getName(),
            userEntry,
            start != null && start instanceof PersonStart ? createPersonStart((PersonStart) start, classStart) : null,
            start != null && start instanceof TeamStart ? createTeamStart((TeamStart) start, classStart) : null,
            null,
            null);
    }

    private static UserCompetitor updateUserResult(Person person, UserEntry userEntry, UserPersonStart personStart, UserTeamStart teamStart, ClassResult classResult, Object result) throws NumberFormatException, ParseException {
        return new UserCompetitor(
            person.getPersonId(),
            person.getName(),
            userEntry,
            personStart,
            teamStart,
            result != null && result instanceof PersonResult ? createPersonResult((PersonResult) result, classResult) : null,
            result != null && result instanceof TeamResult ? createTeamResult((TeamResult) result, classResult) : null
        );
    }

    private static UserEntry createUserEntry(Entry entry, EventClassList eventClassList){
        return new UserEntry(
            entry.getEntryClass() != null && !entry.getEntryClass().isEmpty() ? EventClassConverter.getEventClassFromId(eventClassList ,entry.getEntryClass().get(0).getEventClassId().getContent()) : null,
            entry.getCompetitor().getCCard() != null  && !entry.getCompetitor().getCCard().isEmpty() ? EventConverter.convertCCard(entry.getCompetitor().getCCard().get(0)) : null);
    }

    private static UserPersonStart createPersonStart(PersonStart personStart, ClassStart classStart) {
        Start start = null;
        if(personStart.getStart() != null){
            start = personStart.getStart();
        } else{
            start = personStart.getRaceStart().get(0).getStart();
        }

        return new UserPersonStart(
            start.getStartTime() != null ? StartListConverter.convertStartTime(start.getStartTime()) : null,
            start.getBibNumber() != null ? start.getBibNumber().getContent() : "",
            EventClassConverter.convertEventClass(classStart.getEventClass())
        );
    }

    private static UserTeamStart createTeamStart(TeamStart teamStart, ClassStart classStart) {
        return new UserTeamStart(
            teamStart.getTeamName().getContent(),
            teamStart.getStartTime() != null ? StartListConverter.convertStartTime(teamStart.getStartTime()) : null,
            teamStart.getBibNumber() != null ? teamStart.getBibNumber().getContent() : "",
            teamStart.getTeamMemberStart().get(0).getLeg().intValue(),
            EventClassConverter.convertEventClass(classStart.getEventClass())
        );
    }
    
    public static UserPersonResult createPersonResult(PersonResult personResult, ClassResult classResult) throws NumberFormatException, ParseException  {
        Result result = null;
        if(personResult.getResult() != null){
            result = personResult.getResult();
        } else{
            result = personResult.getRaceResult().get(0).getResult();
        }

        return new UserPersonResult(
            result.getBibNumber() != null ? result.getBibNumber().getContent() : "",
            ResultListConverter.convertPersonResult(result),
            EventClassConverter.convertEventClass(classResult.getEventClass())
        );
    }

    public static UserTeamResult createTeamResult(TeamResult teamResult, ClassResult classResult) throws NumberFormatException, ParseException  {
        return new UserTeamResult(
            teamResult.getTeamName().getContent(),
            teamResult.getBibNumber() != null ? teamResult.getBibNumber().getContent() : "",
            ResultListConverter.convertTeamResult(teamResult),
            teamResult.getTeamMemberResult().get(0).getLeg().intValue(),
            ResultListConverter.convertTimetoSec(teamResult.getTeamMemberResult().get(0).getTime().getContent()),
            EventClassConverter.convertEventClass(classResult.getEventClass())
        );
    }
}