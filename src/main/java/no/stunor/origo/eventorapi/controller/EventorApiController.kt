package no.stunor.origo.eventorapi.controller;

import lombok.extern.slf4j.Slf4j;
import no.stunor.origo.eventorapi.model.event.competitor.Competitor;
import no.stunor.origo.eventorapi.model.person.Person;
import no.stunor.origo.eventorapi.model.event.Event;
import no.stunor.origo.eventorapi.model.calendar.CalendarRace;
import no.stunor.origo.eventorapi.model.origo.entry.EventEntryList;
import no.stunor.origo.eventorapi.model.event.EventClassificationEnum;
import no.stunor.origo.eventorapi.model.origo.result.RaceResultList;
import no.stunor.origo.eventorapi.services.AuthService;
import no.stunor.origo.eventorapi.services.CalendarService;
import no.stunor.origo.eventorapi.services.EventService;
import no.stunor.origo.eventorapi.services.OrganisationService;
import no.stunor.origo.eventorapi.services.OrganiserService;
import no.stunor.origo.eventorapi.services.CompetitorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
class EventorApiController {

    @Autowired
    AuthService authService;
    @Autowired
    CalendarService calendarService;
    @Autowired
    CompetitorService competitorService;
    @Autowired
    EventService eventService;
    @Autowired
    OrganisationService organisationService;
    @Autowired
    OrganiserService organiserService;

    @GetMapping("/authenticate/{eventorId}")
    public ResponseEntity<Person> authenticate(@PathVariable(value = "eventorId") String eventorId, @RequestHeader(value = "username") String username, @RequestHeader(value = "password") String password, @RequestHeader(value = "userId") String userId){
        log.info("Start authenticating user {}.", username);
        return new ResponseEntity<>(authService.authenticate(eventorId, username, password, userId), HttpStatus.OK);
    }

    @GetMapping("/eventList/{eventorId}")
    public ResponseEntity<List<CalendarRace>> getEventList(@PathVariable("eventorId") String eventorId, @RequestParam("from") LocalDate from, @RequestParam("to") LocalDate to, @RequestParam(value = "organisations", required = false) List<String> organisations, @RequestParam(value = "classifications", required = false) List<EventClassificationEnum> classifications, @RequestHeader("userId") String userId) {
        log.info("Start to get event-list from eventor-{}.", eventorId);
        return new ResponseEntity<>(calendarService.getEventList(eventorId, from, to, organisations, classifications, userId), HttpStatus.OK);
    }

    @GetMapping("/eventList")
    public ResponseEntity<List<CalendarRace>> getEventList(@RequestParam("from") LocalDate from, @RequestParam("to") LocalDate to, @RequestParam(value = "classifications", required = false) List<EventClassificationEnum> classifications, @RequestHeader("userId") String userId)  {
        log.info("Start to get event-list from all eventors.");
        return new ResponseEntity<>(calendarService.getEventList(from, to, classifications, userId), HttpStatus.OK);
    }

    @GetMapping("/eventList/user")
    public ResponseEntity<List<CalendarRace>> getUserEntries(@RequestHeader("userId") String userId) {
        log.info("Start to get personal events for user {}.", userId);
        return new ResponseEntity<>(calendarService.getEventList(userId), HttpStatus.OK);
    }


    @GetMapping("/event/{eventorId}/{eventId}")
    public ResponseEntity<Event> getEvent(@PathVariable("eventorId") String eventorId, @PathVariable("eventId") String eventId) {
        return new ResponseEntity<>(eventService.getEvent(eventorId, eventId), HttpStatus.OK);
    }

    @GetMapping("/event/{eventorId}/{eventId}/competitors/{userId}")
    public ResponseEntity<List<Competitor>> getEvent(@PathVariable("eventorId") String eventorId, @PathVariable("eventId") String eventId, @PathVariable("userId") String userId) {
        return new ResponseEntity<>(competitorService.getCompetitors(eventorId, eventId, userId), HttpStatus.OK);
    }

    @GetMapping("/event/{eventorId}/{eventId}/entrylist")
    public ResponseEntity<EventEntryList> getEventEntryList(@PathVariable("eventorId") String eventorId, @PathVariable("eventId") String eventId) {
        return new ResponseEntity<>(eventService.getEntryList(eventorId, eventId), HttpStatus.OK);
    }

    @GetMapping("/event/{eventorId}/{eventId}/startlist")
    public ResponseEntity<List<Competitor>> getEventStartList(@PathVariable("eventorId") String eventorId, @PathVariable("eventId") String eventId) {
        return new ResponseEntity<>(eventService.getStartList(eventorId, eventId), HttpStatus.OK);
    }

    @GetMapping("/event/{eventorId}/{eventId}/resultList")
    public ResponseEntity<List<RaceResultList>> getEventResultList(@PathVariable("eventorId") String eventorId, @PathVariable("eventId") String eventId) {
            return new ResponseEntity<>(eventService.getResultList(eventorId, eventId), HttpStatus.OK);
    }


    @GetMapping("/organisation/apiKey/validate/{eventorId}/{organisationId}")
    public ResponseEntity<Boolean> validateApiKey(@PathVariable("eventorId") String eventorId, @PathVariable("organisationId") String organisationId) {
        return new ResponseEntity<>(organisationService.validateApiKey(eventorId, organisationId), HttpStatus.OK);
    }


    @PostMapping("/organisation/apiKey/{eventorId}/{organisationId}/{apiKey}")
    public void updateApiKey(@PathVariable("eventorId") String eventorId, @PathVariable("organisationId") String organisationId, @PathVariable("apiKey") String apiKey) {
        organisationService.updateApiKey(eventorId, organisationId, apiKey);
    }

    @GetMapping("/organiser/eventList/{eventorId}/{organisationId}")
    public ResponseEntity<List<Event>> getOrganiserEventList(@PathVariable("eventorId") String eventorId, @PathVariable("organisationId") String organisationId)  {
        return new ResponseEntity<>(organiserService.listEvents(eventorId, organisationId), HttpStatus.OK);
    }
}
