package no.stunor.origo.eventorapi.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.iof.eventor.Entry;
import org.iof.eventor.EntryList;
import org.iof.eventor.EventClassList;
import org.iof.eventor.EventRaceId;
import org.iof.eventor.ResultListList;
import org.iof.eventor.StartListList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import no.stunor.origo.eventorapi.api.EventorService;
import no.stunor.origo.eventorapi.api.exception.EventorApiException;
import no.stunor.origo.eventorapi.model.firestore.Eventor;
import no.stunor.origo.eventorapi.model.firestore.Person;
import no.stunor.origo.eventorapi.model.origo.user.UserRace;
import no.stunor.origo.eventorapi.repository.EventorRepository;
import no.stunor.origo.eventorapi.repository.PersonRepository;
import no.stunor.origo.eventorapi.services.converter.PersonEntriesConverter;

@Slf4j
@Service
public class UserEntryService {

    @Autowired
    EventorRepository eventorRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    EventorService eventorService;
    @Autowired
    PersonEntriesConverter personEntriesConverter;

    
    public UserEntryService(EventorRepository eventorRepository, PersonRepository personRepository,
            EventorService eventorService, PersonEntriesConverter personEntriesConverter) {
        this.eventorRepository = eventorRepository;
        this.personRepository = personRepository;
        this.eventorService = eventorService;
        this.personEntriesConverter = personEntriesConverter;
    }




    public  List<UserRace>  userRaces(String userId, Eventor eventor, String eventNumber) throws InterruptedException, ExecutionException, NumberFormatException, ParseException {
        List<UserRace> raceList = new ArrayList<>();

        List<Person> persons = personRepository.findByUsers(userId).collectList().block();

        for (Person person : persons){
            Eventor personEventor = null;
            if(eventor == null){
                personEventor = eventorRepository.findByEventorId(person.getEventor()).blockFirst();
            } else if (!person.getEventor().equals(eventor.getEventorId())){
                continue;
            }
            List<String> organisationIds = new ArrayList<>();
            for(String organisationId : person.getMemberships().keySet()){
                organisationIds.add(organisationId);
            }
  
            EntryList entryList;
            try {
                entryList = eventorService.getGetOrganisationEntries(eventor != null ? eventor : personEventor, organisationIds, eventNumber);
                Map<String, EventClassList> eventClassMap = new HashMap<>();
                for(Entry entry :entryList.getEntry()){
                    for(EventRaceId raceId : entry.getEventRaceId()){
                        if(!eventClassMap.containsKey(raceId.getContent())){
                           EventClassList eventClassList = eventorService.getEventClasses(eventor != null ? eventor : personEventor, entry.getEvent().getEventId().getContent());
                           eventClassMap.put(raceId.getContent(), eventClassList);
                          
                        }
                    }
                }
                StartListList startListList = eventorService.getGetPersonalStarts(eventor != null ? eventor : personEventor, person.getPersonId(), eventNumber);
                ResultListList resultListList = eventorService.getGetPersonalResults(eventor != null ? eventor : personEventor, person.getPersonId(), eventNumber);
                List<UserRace> personRaces = personEntriesConverter.convertPersonEntries(eventor != null ? eventor : personEventor, person, entryList, startListList, resultListList, eventClassMap);

                for(UserRace race : personRaces){
                    boolean raceExist = false;
                    for(UserRace r : raceList){
                        if(race.getEventor().getEventorId().equals(r.getEventor().getEventorId()) && race.getRaceId().equals(r.getRaceId())){
                            raceExist = true;
                            r.getUserCompetitors().addAll(race.getUserCompetitors());
                            r.getOrganisationEntries().putAll(race.getOrganisationEntries());
                        }
                    }
                    if(!raceExist){
                        raceList.add(race);
                    }
                }
            } catch (EventorApiException e) {
                log.warn("Not able to fetch data for person {} from {}.", person.getPersonId(), eventor != null? eventor.getName() : personEventor!= null ? personEventor.getName() : "");
            } 
        }

        return raceList;
    }
}