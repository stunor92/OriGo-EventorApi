package no.stunor.origo.eventorapi.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.iof.eventor.CompetitorCountList;
import org.iof.eventor.DocumentList;
import org.iof.eventor.EntryList;
import org.iof.eventor.Event;
import org.iof.eventor.EventClassList;
import org.iof.eventor.EventList;
import org.iof.eventor.Organisation;
import org.iof.eventor.Person;
import org.iof.eventor.ResultList;
import org.iof.eventor.ResultListList;
import org.iof.eventor.StartList;
import org.iof.eventor.StartListList;

import no.stunor.origo.eventorapi.api.exception.EventorApiException;
import no.stunor.origo.eventorapi.model.firestore.Eventor;
import no.stunor.origo.eventorapi.model.origo.event.EventClassificationEnum;
@Slf4j
@Service
public class EventorService {

    @Value("${config.personalEntries.start}")
    private int perosnalEtriesStart;

    @Value("${config.personalEntries.end}")
    private int perosnalEtriesEnd;

    @Value("${config.personalStarts.start}")
    private int perosnaStartsStart;

    @Value("${config.personalStarts.end}")
    private int perosnalStartsEnd;

    @Value("${config.personalResults.start}")
    private int perosnaResultsStart;

    @Value("${config.personalResults.end}")
    private int perosnalResultsEnd;


    RestTemplate restTemplate;
    public EventorService() {
        restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        rf.setReadTimeout(6000);
        rf.setConnectTimeout(6000);

        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new Jaxb2RootElementHttpMessageConverter());
        restTemplate.setMessageConverters(converters);
    }

    public Person authenticatePerson(Eventor eventor, String username, String password) throws EventorApiException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Username", username);
        headers.set("Password", password);
        try {
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Person> response = restTemplate.exchange(
                    eventor.getBaseUrl() + "api/authenticatePerson",
                    HttpMethod.GET,
                    request,
                    Person.class,
                    1
            );
            return response.getBody();
        } catch (HttpClientErrorException e){
            log.warn(e.getStatusText());
            throw new EventorApiException(e.getStatusCode(), e.getStatusText());
        }

    }
    public EventList getEventList(Eventor eventor, LocalDate fromDate, LocalDate toDate, List<String> organisationIds, List<EventClassificationEnum> classifications) throws EventorApiException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("ApiKey", eventor.getApiKey());

        List<String> classificationIds = new ArrayList<>();

        if(classifications != null){
            for(EventClassificationEnum eventClassificatiion : classifications){
                switch (eventClassificatiion) {
                    case CHAMPIONSHIP:
                        classificationIds.add("1");
                        break;
                    case NATIONAL:
                        classificationIds.add("2");
                        break;
                    case REGIONAL:
                        classificationIds.add("3");
                        break;
                    case LOCAL:
                        classificationIds.add("4");
                        break;
                    case CLUB:
                        classificationIds.add("5");
                        break;
                    default:
                        break;
                }
            }
        }
    
        try {
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<EventList> response = restTemplate.exchange(
                    eventor.getBaseUrl() + "api/events?fromDate=" + (fromDate == null ? "" : DateTimeFormatter.ofPattern("yyyy-MM-dd").format(fromDate)) +
                            "&toDate=" + (toDate == null ? "" : DateTimeFormatter.ofPattern("yyyy-MM-dd").format(toDate)) +
                            "&organisationIds=" + (organisationIds == null ? "" : String.join(",", organisationIds)) +
                            "&classificationIds=" + String.join(",", classificationIds) +
                            "&includeEntryBreaks=true",
                    HttpMethod.GET,
                    request,
                    EventList.class,
                    1
            );
            return response.getBody();
        } catch (HttpClientErrorException e){
            log.warn(e.getStatusText());
            throw new EventorApiException(e.getStatusCode(), e.getStatusText());
        }



    }

       public CompetitorCountList getCompetitorCounts(Eventor eventor, List<String> events, List<String> organisations, List<String> persons) throws EventorApiException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("ApiKey", eventor.getApiKey());

        try {
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<CompetitorCountList> response = restTemplate.exchange(
                    eventor.getBaseUrl() + "api/competitorcount?eventIds=" + String.join(",", events) +
                            ",&organisationIds=" + String.join(",", organisations) + 
                            "&personIds=" + String.join(",", persons),
                    HttpMethod.GET,
                    request,
                    CompetitorCountList.class,
                    1
            );
            return response.getBody();
        } catch (HttpClientErrorException e){
            log.warn(e.getStatusText());
            throw new EventorApiException(e.getStatusCode(), e.getStatusText());
        }
    }

    public StartListList getGetPersonalStarts(Eventor eventor, String personId, String eventId) throws EventorApiException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("ApiKey", eventor.getApiKey());

        String fromDate = "";
        String toDate = "";
        if(eventId == null){
            Calendar start = Calendar.getInstance();
            start.add(Calendar.DATE, perosnaStartsStart);

            Calendar end = Calendar.getInstance();
            end.add(Calendar.DATE, perosnalStartsEnd);
            fromDate = start.get(Calendar.YEAR) + "-" + (start.get(Calendar.MONTH) + 1) + "-" + start.get(Calendar.DAY_OF_MONTH);
            toDate = end.get(Calendar.YEAR) + "-" + (end.get(Calendar.MONTH) + 1) + "-" + end.get(Calendar.DAY_OF_MONTH);

        }

        if(eventId == null){
            eventId = "";
        }

        try {
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<StartListList> response = restTemplate.exchange(
                    eventor.getBaseUrl() + "api/starts/person?personId=" + personId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&eventIds=" + eventId,
                    HttpMethod.GET,
                    request,
                    StartListList.class,
                    1
            );
            return response.getBody();
        } catch (HttpClientErrorException e){
            log.warn(e.getStatusText());
            throw new EventorApiException(e.getStatusCode(), e.getStatusText());
        }
    }

    public ResultListList getGetPersonalResults(Eventor eventor, String personId, String eventNumber) throws EventorApiException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("ApiKey", eventor.getApiKey());

        String fromDate = "";
        String toDate = "";
        if(eventNumber == null){
            Calendar start = Calendar.getInstance();
            start.add(Calendar.DATE, perosnaResultsStart);

            Calendar end = Calendar.getInstance();
            end.add(Calendar.DATE, perosnalResultsEnd);


            fromDate = start.get(Calendar.YEAR) + "-" + (start.get(Calendar.MONTH) + 1) + "-" + start.get(Calendar.DAY_OF_MONTH);
            toDate = end.get(Calendar.YEAR) + "-" + (end.get(Calendar.MONTH) + 1) + "-" + end.get(Calendar.DAY_OF_MONTH);

        }

        if(eventNumber == null){
            eventNumber = "";
        }

        try {
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<ResultListList> response = restTemplate.exchange(
                    eventor.getBaseUrl() + "api/results/person?personId=" + personId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&eventIds=" + eventNumber ,
                    HttpMethod.GET,
                    request,
                    ResultListList.class,
                    1
            );
            return response.getBody();
        } catch (HttpClientErrorException e){
            log.warn(e.getStatusText());
            throw new EventorApiException(e.getStatusCode(), e.getStatusText());
        }
    }

    public EntryList getGetOrganisationEntries(Eventor eventor, List<String> organisations, String eventNumber) throws EventorApiException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("ApiKey", eventor.getApiKey());

        String fromDate = "";
        String toDate = "";
        if(eventNumber == null){
            Calendar start = Calendar.getInstance();
            start.add(Calendar.DATE, perosnalEtriesStart);

            Calendar end = Calendar.getInstance();
            end.add(Calendar.DATE, perosnalEtriesEnd);
            
            fromDate = start.get(Calendar.YEAR) + "-" + (start.get(Calendar.MONTH) + 1) + "-" + start.get(Calendar.DAY_OF_MONTH);
            toDate = end.get(Calendar.YEAR) + "-" + (end.get(Calendar.MONTH) + 1) + "-" + end.get(Calendar.DAY_OF_MONTH);

        }

        if(eventNumber == null){
            eventNumber = "";
        }

        try {
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<EntryList> response = restTemplate.exchange(
                    eventor.getBaseUrl() + "api/entries?organisationIds=" + String.join(",", organisations) + "&fromEventDate=" + fromDate + "&toEventDate=" + toDate + "&includeEventElement=true&eventIds=" + eventNumber,
                    HttpMethod.GET,
                    request,
                    EntryList.class,
                    1
            );
            return response.getBody();
        } catch (HttpClientErrorException e){
            log.warn(e.getStatusText());
            throw new EventorApiException(e.getStatusCode(), e.getStatusText());
        }
    }

    public Event getEvent(String baseUrl, String apiKey, String eventId) throws EventorApiException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("ApiKey", apiKey);

        try {
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Event> response = restTemplate.exchange(
                    baseUrl + "api/event/" + eventId,
                    HttpMethod.GET,
                    request,
                    Event.class,
                    1
            );
            return response.getBody();
        } catch (HttpClientErrorException e){
            log.warn(e.getStatusText());
            throw new EventorApiException(e.getStatusCode(), e.getStatusText());
        }
    }

    public EventClassList getEventClasses(Eventor eventor, String eventId) throws EventorApiException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("ApiKey", eventor.getApiKey());

        try {
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<EventClassList> response = restTemplate.exchange(
                    eventor.getBaseUrl() + "api/eventclasses?includeEntryFees=true&eventId=" + eventId,
                    HttpMethod.GET,
                    request,
                    EventClassList.class,
                    1
            );
            return response.getBody();
        } catch (HttpClientErrorException e){
            log.warn(e.getStatusText());
            throw new EventorApiException(e.getStatusCode(), e.getStatusText());
        }
    }

    public DocumentList getEventDocuments(String baseUrl, String apiKey, String eventId) throws EventorApiException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("ApiKey", apiKey);

        try{
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<DocumentList> response = restTemplate.exchange(
                    baseUrl+ "api/events/documents?eventIds=" + eventId,
                    HttpMethod.GET,
                    request,
                    DocumentList.class,
                    1
            );
            return response.getBody();
        } catch (HttpClientErrorException e){
            log.warn(e.getStatusText());
            throw new EventorApiException(e.getStatusCode(), e.getStatusText());
        }
    }

    public EntryList getEventEntryList(String baseUrl, String apiKey, String eventId) throws EventorApiException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("ApiKey", apiKey);

        try {
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<EntryList> response = restTemplate.exchange(
                    baseUrl + "api/entries?includePersonElement=true&includeEntryFees=true&includeOrganisationElement=true&eventIds=" + eventId,
                    HttpMethod.GET,
                    request,
                    EntryList.class,
                    1
            );
            return response.getBody();
        } catch (HttpClientErrorException e){
            log.warn(e.getStatusText());
            throw new EventorApiException(e.getStatusCode(), e.getStatusText());
        }
    }

    public StartList getEventStartList(String baseUrl, String apiKey, String eventId) throws EventorApiException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("ApiKey", apiKey);

        try {
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<StartList> response = restTemplate.exchange(
                    baseUrl + "api/starts/event?eventId=" + eventId,
                    HttpMethod.GET,
                    request,
                    StartList.class,
                    1
            );
            return response.getBody();
        } catch (HttpClientErrorException e){
            log.warn(e.getStatusText());
            throw new EventorApiException(e.getStatusCode(), e.getStatusText());
        }
    }

    public ResultList getEventResultList(String baseUrl, String apiKey, String eventId) throws EventorApiException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("ApiKey", apiKey);

        try {
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<ResultList> response = restTemplate.exchange(
                    baseUrl + "api/results/event?eventId=" + eventId + "&includeSplitTimes=true",
                    HttpMethod.GET,
                    request,
                    ResultList.class,
                    1
            );
            return response.getBody();
        } catch (HttpClientErrorException e){
            log.warn(e.getStatusText());
            throw new EventorApiException(e.getStatusCode(), e.getStatusText());
        }
    }

    public Organisation getOrganisatonFromApiKey(String baseUrl, String apiKey) throws EventorApiException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("ApiKey", apiKey);

        try {
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Organisation> response = restTemplate.exchange(
                    baseUrl + "api/organisation/apiKey",
                    HttpMethod.GET,
                    request,
                    Organisation.class,
                    1
            );
            return response.getBody();
        } catch (HttpClientErrorException e){
            if(e.getStatusCode().value() == 404){
                throw new EventorApiException(e.getStatusCode(), "ApiKey does not belog to any organisation.");
            }
            log.warn(e.getStatusText());
            throw new EventorApiException(e.getStatusCode(), e.getStatusText());
        }
    }
}
