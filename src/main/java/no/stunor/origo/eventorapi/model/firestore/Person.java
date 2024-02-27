package no.stunor.origo.eventorapi.model.firestore;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.stunor.origo.eventorapi.model.origo.person.PersonName;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collectionName = "persons")
public class Person {
    @JsonIgnore
    @DocumentId
    private String id;
    private String eventor;
    private String personId;
    private PersonName name;
    private Integer birthYear;
    private String nationality;
    private String gender;
    private List<String> users;
    private Map<String, String> memberships;
    private String mobilePhone;
    private String email;
}
