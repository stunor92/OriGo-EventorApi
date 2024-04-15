package no.stunor.origo.eventorapi.services.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iof.eventor.Role;

import no.stunor.origo.eventorapi.model.firestore.Eventor;
import no.stunor.origo.eventorapi.model.firestore.Person;
import no.stunor.origo.eventorapi.model.origo.person.CompetitorPerson;
import no.stunor.origo.eventorapi.model.origo.person.PersonName;

public class PersonConverter {

    public static Person convertPerson(org.iof.eventor.Person eventorPerson, Eventor eventor) {
        return new Person(
            null,
            eventor.getEventorId(),
            eventorPerson.getPersonId().getContent(),
            convertPersonName(eventorPerson.getPersonName()),
            eventorPerson.getBirthDate()!= null ? Integer.parseInt(eventorPerson.getBirthDate().getDate().getContent().substring(0,4)) : null,
            eventorPerson.getNationality() != null ? eventorPerson.getNationality().getCountry().getAlpha3().getValue() : null,
            convertGender(eventorPerson.getSex()),
            new ArrayList<>(),
            convertMemberships(eventorPerson.getRole()),
            ContactConverter.convertPhone(eventorPerson.getTele()),
            ContactConverter.convertEmail(eventorPerson.getTele())
        );
    }

    public static CompetitorPerson convertCompetitor(org.iof.eventor.Person person, Eventor eventor) {
        return new CompetitorPerson(
            eventor.getEventorId(),
            person.getPersonId().getContent(),
            convertPersonName(person.getPersonName()),
            person.getBirthDate()!= null ? Integer.parseInt(person.getBirthDate().getDate().getContent().substring(0,4)) : null,
            person.getNationality() != null && person.getNationality().getCountry() != null ? person.getNationality().getCountry().getAlpha3().getValue() : null,
            convertGender(person.getSex())
        );
    }

    private static String convertGender(String sex) {
        if(sex == null){
            return "OTHER";
        }
        switch(sex){
            case "M" : return "MAN";
            case "F" : return "WOMAN";
            default  : return "OTHER";
        }
    }

    public static PersonName convertPersonName(org.iof.eventor.PersonName personName) {
        StringBuilder given = new StringBuilder();
        for(int i = 0; i < personName.getGiven().size(); i++) {
            for (int j = 1; j <= personName.getGiven().size(); j++) {
                if(Integer.parseInt(personName.getGiven().get(i).getSequence()) == j){
                    if(!given.toString().equals("")){
                        given.append(" ");
                    }
                    given.append(personName.getGiven().get(i).getContent());
                }
            }
        }

        return new PersonName(personName.getFamily().getContent(), given.toString());
    }
    private static Map<String, String> convertMemberships(List<Role> roles) {
        Map<String, Integer> highestRole= new HashMap<>();

        for (Role role : roles){
            if(role.getRoleTypeId().getContent().equals("2")){
                role.getRoleTypeId().setContent("10");;
            }

            if(!highestRole.containsKey(role.getOrganisationId().getContent())){
                highestRole.put(role.getOrganisationId().getContent(), Integer.parseInt(role.getRoleTypeId().getContent()));
            } else if(Integer.parseInt(role.getRoleTypeId().getContent()) > highestRole.get(role.getOrganisationId().getContent())){
                highestRole.put(role.getOrganisationId().getContent(), Integer.parseInt(role.getRoleTypeId().getContent()));
            }
        }

        Map<String, String> memberships = new HashMap<>();

        for(String orgId : highestRole.keySet().stream().toList()){
            if(highestRole.get(orgId) == 1){
                memberships.put(orgId, "MEMBER");
            } else if (highestRole.get(orgId) == 3 || highestRole.get(orgId) == 5) {
                memberships.put(orgId, "ORGANISER");
            } else if (highestRole.get(orgId) == 10) {
                memberships.put(orgId, "ADMIN");
            }
        }


        return memberships;
    }
    
}
