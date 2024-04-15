package no.stunor.origo.eventorapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.iof.eventor.EntryList;
import org.iof.eventor.ResultListList;
import org.iof.eventor.StartListList;
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
import no.stunor.origo.eventorapi.data.PersonRepository;
import no.stunor.origo.eventorapi.model.firestore.Eventor;
import no.stunor.origo.eventorapi.model.firestore.Person;
import no.stunor.origo.eventorapi.model.origo.person.PersonName;
import no.stunor.origo.eventorapi.services.UserEntryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
public class PersonalEventsServiceTest {

    @Autowired
    UserEntryService userEntryService;

    @MockBean
    EventorService eventorService;
    
    @MockBean
    PersonRepository personRepository;

    @MockBean
    EventorRepository eventorRepository;

    @BeforeEach
    public void setUp() {
        when(personRepository.findAllByUsersContains(anyString())).thenReturn(Flux.fromArray(new Person[] {generatePerson()}));
        when(eventorRepository.findByEventorId(anyString())).thenReturn(Mono.just(generateEventor()));
    }
   
    @Test                                                                                          
    public void testPersonResultInactive() throws JAXBException, InterruptedException, ExecutionException, NumberFormatException, ParseException {
        when(eventorService.getGetOrganisationEntries(any(Eventor.class), anyList(), any())).thenReturn(generateEntryListFromXml("src/test/resources/eventorResponse/personalEventsService/resultInactive/OrganisationEntries.xml"));
        when(eventorService.getGetPersonalResults(any(Eventor.class), anyString(), any())).thenReturn(generateResultListListFromXml("src/test/resources/eventorResponse/personalEventsService/resultInactive/PersonalResult.xml"));
        when(eventorService.getGetPersonalStarts(any(Eventor.class), anyString(), any())).thenReturn(generateStartListListFromXml("src/test/resources/eventorResponse/personalEventsService/resultInactive/PersonalStart.xml"));


        userEntryService.userRaces("abc", generateEventor(), "123");
    }



    @Test                                                                                          
    public void testPersonNotSignedUp() throws JAXBException, InterruptedException, ExecutionException, NumberFormatException, ParseException {
        when(eventorService.getGetOrganisationEntries(any(Eventor.class), anyList(), any())).thenReturn(generateEntryListFromXml("src/test/resources/eventorResponse/personalEventsService/notSignedUp/OrganisationEntries.xml"));
        when(eventorService.getGetPersonalResults(any(Eventor.class), anyString(), any())).thenReturn(generateResultListListFromXml("src/test/resources/eventorResponse/personalEventsService/notSignedUp/PersonalResult.xml"));
        when(eventorService.getGetPersonalStarts(any(Eventor.class), anyString(), any())).thenReturn(generateStartListListFromXml("src/test/resources/eventorResponse/personalEventsService/notSignedUp/PersonalStart.xml"));

        userEntryService.userRaces("abc", generateEventor(), "123");
    }

/* 
   @Test                                                                                          
    public void testPersonSignedUp() throws EntityNotFoundException, EventorApiException, JAXBException, InterruptedException, ExecutionException, NumberFormatException, ParseException {
        when(eventorService.getGetOrganisationEntries(any(Eventor.class), anyList(), any())).thenReturn(generateEntryListFromXml("src/test/resources/eventorResponse/personalEventsService/signedUp/OrganisationEntries.xml"));
        when(eventorService.getGetPersonalResults(any(Eventor.class), anyString(), any())).thenReturn(generateResultListListFromXml("src/test/resources/eventorResponse/personalEventsService/signedUp/PersonalResult.xml"));
        when(eventorService.getGetPersonalStarts(any(Eventor.class), anyString(), any())).thenReturn(generateStartListListFromXml("src/test/resources/eventorResponse/personalEventsService/signedUp/PersonalStart.xml"));

        userEntryService.userRaces("abc", generateEventor(), "123");
    }
*/

    @Test                                                                                          
    public void testPersonStarttime() throws JAXBException, InterruptedException, ExecutionException, NumberFormatException, ParseException {
        when(eventorService.getGetOrganisationEntries(any(Eventor.class), anyList(), any())).thenReturn(generateEntryListFromXml("src/test/resources/eventorResponse/personalEventsService/personStartTime/OrganisationEntries.xml"));
        when(eventorService.getGetPersonalResults(any(Eventor.class), anyString(), any())).thenReturn(generateResultListListFromXml("src/test/resources/eventorResponse/personalEventsService/personStartTime/PersonalResult.xml"));
        when(eventorService.getGetPersonalStarts(any(Eventor.class), anyString(), any())).thenReturn(generateStartListListFromXml("src/test/resources/eventorResponse/personalEventsService/personStartTime/PersonalStart.xml"));

        userEntryService.userRaces("abc", generateEventor(), "123");
    }


    private static Person generatePerson(){

        PersonName name = new PersonName("Olsen", "Peter");
      
        List<String> owner = new ArrayList<>();
        owner.add("123");

        return new Person(null, "NOR", "123", name, 1900, "NOR", "MAN", owner, new HashMap<>(), "12345678", "a@b.no");
    }
    
    private static Eventor generateEventor(){
        return new Eventor("NOR", "Norge","NOF", "eventor.no", "123abc");
    }

    private EntryList generateEntryListFromXml(String path) throws JAXBException{
   
        File file = new File(path);  
        JAXBContext jaxbContext = JAXBContext.newInstance(EntryList.class);  

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
        return (EntryList) jaxbUnmarshaller.unmarshal(file);  
    }

    private ResultListList generateResultListListFromXml(String path) throws JAXBException{
   
        File file = new File(path);  
        JAXBContext jaxbContext = JAXBContext.newInstance(ResultListList.class);  

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
        return (ResultListList) jaxbUnmarshaller.unmarshal(file);  
    }
   private StartListList generateStartListListFromXml(String path) throws JAXBException{
   
        File file = new File(path);  
        JAXBContext jaxbContext = JAXBContext.newInstance(StartListList.class);  

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
        return (StartListList) jaxbUnmarshaller.unmarshal(file);  
    }

}
