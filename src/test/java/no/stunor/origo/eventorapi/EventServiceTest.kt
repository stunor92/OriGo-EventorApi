package no.stunor.origo.eventorapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import no.stunor.origo.eventorapi.model.organisation.OrganisationType;
import org.iof.eventor.DocumentList;
import org.iof.eventor.Event;
import org.iof.eventor.EventClassList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import no.stunor.origo.eventorapi.api.EventorService;
import no.stunor.origo.eventorapi.data.EventorRepository;
import no.stunor.origo.eventorapi.data.OrganisationRepository;
import no.stunor.origo.eventorapi.data.RegionRepository;
import no.stunor.origo.eventorapi.model.Eventor;
import no.stunor.origo.eventorapi.model.organisation.Organisation;
import no.stunor.origo.eventorapi.model.Region;
import no.stunor.origo.eventorapi.services.EventService;
import reactor.core.publisher.Mono;

@SpringBootTest
public class EventServiceTest {

    @Autowired
    EventService eventService;

    @MockBean
    EventorService eventorService;
    
    @MockBean
    OrganisationRepository organisationRepository;

    @MockBean
    RegionRepository regionRepository;

    @MockBean
    EventorRepository eventorRepository;
   
    @BeforeEach
    public void setUp() {
        when(organisationRepository.findByOrganisationIdAndEventorId(anyString(), anyString())).thenReturn(Mono.just(generateOrganisation()));
        when(regionRepository.findByRegionIdAndEventorId(anyString(), anyString())).thenReturn(Mono.just(generateRegion()));
        when(eventorRepository.findByEventorId(anyString())).thenReturn(Mono.just(generateEventor()));
    }

    @Test                                                                                          
    public void testSingelRaceEvent() throws JAXBException, InterruptedException, ExecutionException, NumberFormatException, ParseException {
        when(eventorService.getEvent(anyString(), anyString(), anyString())).thenReturn(generateEventFromXml("src/test/resources/eventorResponse/eventService/oneDayEvent/Event.xml"));
        when(eventorService.getEventClasses(any(Eventor.class), anyString())).thenReturn(generateEventClassListFromXml("src/test/resources/eventorResponse/eventService/oneDayEvent/EventClassList.xml"));
        when(eventorService.getEventDocuments(anyString(), anyString(),anyString())).thenReturn(generateDocumentListFromXml("src/test/resources/eventorResponse/eventService/oneDayEvent/DocumentList.xml"));
        eventService.getEvent("NOR", "17535", null);
    }

   @Test                                                                                          
    public void testMultiRaceEvent() throws JAXBException, InterruptedException, ExecutionException, NumberFormatException, ParseException {
        when(eventorService.getEvent(anyString(), anyString(), anyString())).thenReturn(generateEventFromXml("src/test/resources/eventorResponse/eventService/multiDaysEvent/Event.xml"));
        when(eventorService.getEventClasses(any(Eventor.class), anyString())).thenReturn(generateEventClassListFromXml("src/test/resources/eventorResponse/eventService/multiDaysEvent/EventClassList.xml"));
        when(eventorService.getEventDocuments(anyString(), anyString(),anyString())).thenReturn(generateDocumentListFromXml("src/test/resources/eventorResponse/eventService/multiDaysEvent/DocumentList.xml"));

        eventService.getEvent("NOR", "18527", null);
    }


    @Test                                                                                          
    public void testRelay() throws JAXBException, InterruptedException, ExecutionException, NumberFormatException, ParseException {
        when(eventorService.getEvent(anyString(), anyString(), anyString())).thenReturn(generateEventFromXml("src/test/resources/eventorResponse/eventService/relayEvent/Event.xml"));
        when(eventorService.getEventClasses(any(Eventor.class),anyString())).thenReturn(generateEventClassListFromXml("src/test/resources/eventorResponse/eventService/relayEvent/EventClassList.xml"));
        when(eventorService.getEventDocuments(anyString(), anyString(),anyString())).thenReturn(generateDocumentListFromXml("src/test/resources/eventorResponse/eventService/relayEvent/DocumentList.xml"));

        eventService.getEvent("NOR", "17469", null);
    }

    private static Organisation generateOrganisation(){
        return new Organisation("141", "NOR", "IL Gneist", OrganisationType.CLUB, "NOR", null,  null, null, null);
    }

    private static Region generateRegion(){
        return new Region( "8", "NOR", "Hordaland");
    }


    private static Eventor generateEventor(){
        return new Eventor("NOR", "Norge","NOF", "eventor.no", "123abc");
    }
    
    private Event generateEventFromXml(String path) throws JAXBException{
   
        File file = new File(path);  
        JAXBContext jaxbContext = JAXBContext.newInstance(Event.class);  

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
        return (Event) jaxbUnmarshaller.unmarshal(file);  
    }

    private EventClassList generateEventClassListFromXml(String path) throws JAXBException{
   
        File file = new File(path);  
        JAXBContext jaxbContext = JAXBContext.newInstance(EventClassList.class);  

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
        return (EventClassList) jaxbUnmarshaller.unmarshal(file);  
    }

    private DocumentList generateDocumentListFromXml(String path) throws JAXBException{
   
        File file = new File(path);  
        JAXBContext jaxbContext = JAXBContext.newInstance(DocumentList.class);  

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
        return (DocumentList) jaxbUnmarshaller.unmarshal(file);  
    }

}

