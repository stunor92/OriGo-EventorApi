package no.stunor.origo.eventorapi.controller;

import lombok.extern.slf4j.Slf4j;
import no.stunor.origo.eventorapi.api.exception.EntityNotFoundException;
import no.stunor.origo.eventorapi.api.exception.EventorApiException;
import no.stunor.origo.eventorapi.model.firestore.Person;
import no.stunor.origo.eventorapi.model.origo.calendar.CalendarRace;
import no.stunor.origo.eventorapi.model.origo.entry.EventEntryList;
import no.stunor.origo.eventorapi.model.origo.event.Event;
import no.stunor.origo.eventorapi.model.origo.event.EventClassificationEnum;
import no.stunor.origo.eventorapi.model.origo.result.RaceResultList;
import no.stunor.origo.eventorapi.model.origo.start.RaceStartList;
import no.stunor.origo.eventorapi.model.origo.user.UserRace;
import no.stunor.origo.eventorapi.services.AuthService;
import no.stunor.origo.eventorapi.services.CalendarService;
import no.stunor.origo.eventorapi.services.EventService;
import no.stunor.origo.eventorapi.services.UserEntryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
class EventorApiController {

    @Autowired
    AuthService authService;
    @Autowired
    CalendarService calendarService;
    @Autowired  
    UserEntryService userEntryService;
    @Autowired
    EventService eventService;

    @GetMapping("/authenticate/{eventorId}")
    public ResponseEntity<Person> authenticate(
        @PathVariable(value = "eventorId") String eventorId,
        @RequestHeader(value = "username") String username,
        @RequestHeader(value = "password") String password,
        @RequestHeader(value = "userId") String userId)

         throws ExecutionException, InterruptedException, IOException {
        
        log.info("Start authenticating user {}.", username);

        try {
             Person person = authService.authenticate(eventorId, username, password, userId);
             return new ResponseEntity<>(person, HttpStatus.OK);
        } catch (EventorApiException e) {
            if(e.getStatusCode() == HttpStatusCode.valueOf(403) ){
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @GetMapping("/eventList/{eventorId}")
    public ResponseEntity<List<CalendarRace>> getEventList(
            @PathVariable("eventorId") String eventorId,
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to,
            @RequestParam(value = "organisations", required = false) List<String> organisations,
            @RequestParam(value = "classifications", required = false) List<EventClassificationEnum> classifications,
            @RequestHeader("userId") String userId) throws ExecutionException, InterruptedException {

        log.info("Start to get eventlist from eventor-{}.", eventorId);

        try {
             List<CalendarRace> raceList = calendarService.getEventList(eventorId, from, to, organisations, classifications, userId);
             return new ResponseEntity<>(raceList, HttpStatus.OK);
        } catch (EventorApiException e) {
            if(e.getStatusCode() == HttpStatusCode.valueOf(403) ){
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

    }
    @GetMapping("/eventList")
    public ResponseEntity<List<CalendarRace>> getEventList(
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to,
            @RequestParam(value = "classifications", required = false) List<EventClassificationEnum> classifications,
            @RequestHeader("userId") String userId) throws ExecutionException, InterruptedException {

        log.info("Start to get eventlist from all eventors.");

        try {
             List<CalendarRace> raceList = calendarService.getEventList(from, to, classifications, userId);
             return new ResponseEntity<>(raceList, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping("/personalEvents")
    public ResponseEntity<List<UserRace>> getPersonalEvents(@RequestHeader("userId") String userId) throws NumberFormatException, ParseException {
        log.info("Start to get personal events for user {}.", userId);
        try {
            List<UserRace> raceList;
            raceList = userEntryService.userRaces(userId, null, null);
            return new ResponseEntity<>(raceList, HttpStatus.OK);
        } catch (InterruptedException | ExecutionException e) {
            return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping("/event/{eventorId}/{eventId}")
    public ResponseEntity<Event> getEvent(
            @PathVariable("eventorId") String eventorId,
            @PathVariable("eventId") String eventId,
            @RequestHeader("userId") String userId) throws ExecutionException, InterruptedException, NumberFormatException, ParseException {
        try {
            return new ResponseEntity<>(eventService.getEvent(eventorId, eventId, userId), HttpStatus.OK);
        } catch (EventorApiException e) {
            if(e.getStatusCode() == HttpStatusCode.valueOf(403) ){
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);        
    }

    @GetMapping("/event/entrylist/{eventorId}/{eventId}")
    public ResponseEntity<EventEntryList> getEventEntryList(
            @PathVariable("eventorId") String eventorId,
            @PathVariable("eventId") String eventId) throws ExecutionException, InterruptedException {                
        try {
            return new ResponseEntity<>(eventService.getEntryList(eventorId, eventId), HttpStatus.OK);
        } catch (EventorApiException e) {
            if(e.getStatusCode() == HttpStatusCode.valueOf(403) ){
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);      
    }

    @GetMapping("/event/startlist/{eventorId}/{eventId}")
    public ResponseEntity<List<RaceStartList>> getEventStartList(
            @PathVariable("eventorId") String eventorId,
            @PathVariable("eventId") String eventId) throws ExecutionException, InterruptedException {

        try {
            return new ResponseEntity<>(eventService.getStartList(eventorId, eventId), HttpStatus.OK);
        } catch (EventorApiException e) {
            if(e.getStatusCode() == HttpStatusCode.valueOf(403) ){
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);    
    }

    @GetMapping("/event/resultlist/{eventorId}/{eventId}")
    public ResponseEntity<List<RaceResultList>> getEventResultList(
            @PathVariable("eventorId") String eventorId,
            @PathVariable("eventId") String eventId) throws ExecutionException, InterruptedException, NumberFormatException, ParseException {

         try {
            return new ResponseEntity<>(eventService.getResultList(eventorId, eventId), HttpStatus.OK);
        } catch (EventorApiException e) {
            if(e.getStatusCode() == HttpStatusCode.valueOf(403) ){
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);   
    }

}
