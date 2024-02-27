package no.stunor.origo.eventorapi.model.firestore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.cloud.Timestamp;
import com.google.cloud.spring.data.firestore.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collectionName = "organisations")
public class Organisation {
    private String organisationId;
    private String eventor;
    private String name;
    private String type;
    private String country;
    @JsonIgnore
    private String email;
    @JsonIgnore
    private String region;
    @JsonIgnore
    private String contactPerson;
    @JsonIgnore
    private Timestamp lastUpdated;
}