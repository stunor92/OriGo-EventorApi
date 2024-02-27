package no.stunor.origo.eventorapi.model.firestore;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Document(collectionName = "eventors")
public class Eventor {
    private String eventorId;
    private String name;
    private String federation;
    private String baseUrl;
    @JsonIgnore
    private String apiKey;
}