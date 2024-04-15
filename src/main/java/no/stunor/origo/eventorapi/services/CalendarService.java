package no.stunor.origo.eventorapi.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.iof.eventor.CompetitorCount;
import org.iof.eventor.CompetitorCountList;
import org.iof.eventor.Event;
import org.iof.eventor.EventList;
import org.iof.eventor.EventRace;
import org.iof.eventor.OrganisationCompetitorCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import no.stunor.origo.eventorapi.api.EventorService;
import no.stunor.origo.eventorapi.api.exception.EntityNotFoundException;
import no.stunor.origo.eventorapi.api.exception.EventorApiException;
import no.stunor.origo.eventorapi.data.EventorRepository;
import no.stunor.origo.eventorapi.data.PersonRepository;
import no.stunor.origo.eventorapi.model.firestore.Eventor;
import no.stunor.origo.eventorapi.model.firestore.Person;
import no.stunor.origo.eventorapi.model.origo.calendar.CalendarRace;
import no.stunor.origo.eventorapi.model.origo.event.EventClassificationEnum;
import no.stunor.origo.eventorapi.services.converter.EventConverter;


@Slf4j
@Service
public class CalendarService {
    @Autowired
    EventorRepository eventorRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    EventorService eventorService;
    @Autowired
    EventConverter eventConverter;

    public List<CalendarRace> getEventList(LocalDate from, LocalDate to, List<EventClassificationEnum> classifications, String userId) throws EntityNotFoundException, InterruptedException, ExecutionException {
        List<Eventor> eventorList = eventorRepository.findAll().collectList().block();
        
        List<CalendarRace> result = new ArrayList<>();

        for(Eventor eventor : eventorList){
            List<Person> persons = personRepository.findAllByUsersContainsAndEventorId(userId, eventor.getEventorId()).collectList().block();
            result.addAll(getEventList(eventor, from, to, null, classifications, persons));
        }
        return result;
    }

    public List<CalendarRace> getEventList(String eventorId, LocalDate from, LocalDate to, List<String> organisations, List<EventClassificationEnum> classifications, String userId) throws EntityNotFoundException, EventorApiException, InterruptedException, ExecutionException {
        Eventor eventor = eventorRepository.findByEventorId(eventorId).block();
        List<Person> persons = personRepository.findAllByUsersContainsAndEventorId(userId, eventor.getEventorId()).collectList().block();
        
        return getEventList(eventor, from, to, organisations, classifications, persons);
    }

    private List<CalendarRace> getEventList(Eventor eventor, LocalDate from, LocalDate to, List<String> organisations, List<EventClassificationEnum> classifications, List<Person> persons) {
        try {
            EventList eventList = eventorService.getEventList(eventor, from, to, organisations, classifications);
            List<String> events = new ArrayList<>();
            for (Event event : eventList.getEvent()){
                events.add(event.getEventId().getContent());
            }

            List<String> personIds = new ArrayList<>();
            List<String> organisationIds = new ArrayList<>();

            for (Person person : persons){
                personIds.add(person.getPersonId());
                organisationIds.addAll(person.getMemberships().keySet());
            }

            log.info("Fetcing cometitorcount for persons {} and orgaisations {}.", personIds, organisationIds);
            CompetitorCountList competitorCountList = eventorService.getCompetitorCounts(eventor, events, organisationIds, personIds);
            return convertEvents(eventList, eventor, competitorCountList);
        } catch (EventorApiException e) {
            log.warn("Not able to fetch evntlist from {}.", eventor.getName());
            return new ArrayList<>();
        }
        
        
    }

    private List<CalendarRace> convertEvents(EventList eventList, Eventor eventor, CompetitorCountList competitorCountList) {
        List<CalendarRace> result = new ArrayList<>();
        for(Event event : eventList.getEvent()){
            result.addAll(convertEvent(event, eventor, competitorCountList));
        }
        return result;
    }

    private List<CalendarRace> convertEvent(Event event, Eventor eventor, CompetitorCountList competitorCountList) {
        List<CalendarRace> result = new ArrayList<>();
        for(EventRace eventRace : event.getEventRace()){
            result.add(convertRace(event, eventRace, eventor, competitorCountList));
        }
        return result;
    }

    private CalendarRace convertRace(Event event, EventRace eventRace, Eventor eventor, CompetitorCountList competitorCountList) {
        return new CalendarRace(
                eventor,
                event.getEventId().getContent(),
                eventRace.getEventRaceId().getContent(),
                event.getName().getContent(),
                eventRace.getName().getContent(),
                eventRace.getRaceDate() != null ? eventConverter.convertRaceDateWhitoutTime(eventRace.getRaceDate()) : null,
                eventConverter.convertEventForm(event.getEventForm()),
                eventConverter.convertEventClassification(event.getEventClassificationId().getContent()),
                eventConverter.convertLightCondition(eventRace.getRaceLightCondition()),
                eventConverter.convertRaceDistance(eventRace.getRaceDistance()),
                eventRace.getEventCenterPosition() != null ? EventConverter.convertPosition(eventRace.getEventCenterPosition()) : null,
                eventConverter.convertEventStatus(event.getEventStatusId().getContent()),
                eventConverter.convertEventDisciplines(event.getDisciplineIdOrDiscipline()),
                eventConverter.convertOrganisers(eventor, event.getOrganiser().getOrganisationIdOrOrganisation()),
                eventConverter.convertEntryBreaks(event.getEntryBreak()),
                isSignedUp(event.getEventId().getContent(), competitorCountList),
                getEntries(event.getEventId().getContent(), eventRace.getEventRaceId().getContent(), competitorCountList),
                getOrganisationEntries(eventor, event.getEventId().getContent(), eventRace.getEventRaceId().getContent(), competitorCountList),
                eventConverter.hasStartList(event.getHashTableEntry(), eventRace.getEventRaceId().getContent()),
                eventConverter.hasResultList(event.getHashTableEntry(), eventRace.getEventRaceId().getContent()),
                eventConverter.hasLivelox(event.getHashTableEntry()));
    }

    private static boolean isSignedUp(String eventId, CompetitorCountList competitorCountList) {
        for(CompetitorCount competitorCount : competitorCountList.getCompetitorCount()) {
            if(competitorCount.getEventId().equals(eventId) && competitorCount.getClassCompetitorCount() != null && !competitorCount.getClassCompetitorCount().isEmpty()){
                return true;
            }
        }
        return false;
    }

    private static Integer getEntries(String eventId, String eventRaceId, CompetitorCountList competitorCountList) {

        for(CompetitorCount competitorCount : competitorCountList.getCompetitorCount()) {
            if(competitorCount.getEventId().equals(eventId)){
                if(competitorCount.getEventRaceId() == null){
                    return Integer.parseInt(competitorCount.getNumberOfEntries());
                } else if(competitorCount.getEventRaceId().equals(eventRaceId)){
                    return Integer.parseInt(competitorCount.getNumberOfEntries());
                }
            }
        }
        return 0;
    }

    private static Map<String,Integer> getOrganisationEntries(Eventor eventor, String eventId, String eventRaceId, CompetitorCountList competitorCountList) {
        Map<String,Integer> result = new HashMap<>();

        for(CompetitorCount competitorCount : competitorCountList.getCompetitorCount()) {
            if(competitorCount.getEventId().equals(eventId)){
                if(competitorCount.getEventRaceId() == null){
                    if(competitorCount.getOrganisationCompetitorCount() != null){
                            for (OrganisationCompetitorCount organisationCompetitorCount : competitorCount.getOrganisationCompetitorCount()){
                                result.put(organisationCompetitorCount.getOrganisationId(), Integer.parseInt(organisationCompetitorCount.getNumberOfEntries()));
                            }
                        }
                } else if(competitorCount.getEventRaceId().equals(eventRaceId)){
                    if(competitorCount.getOrganisationCompetitorCount() != null){
                            for (OrganisationCompetitorCount organisationCompetitorCount : competitorCount.getOrganisationCompetitorCount()){
                                result.put(organisationCompetitorCount.getOrganisationId(), Integer.parseInt(organisationCompetitorCount.getNumberOfEntries()));
                            }
                        }
                }
            }
                        
        }
        return result;
    }

     

    
    
}
