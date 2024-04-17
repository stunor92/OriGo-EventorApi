package no.stunor.origo.eventorapi.model.firestore;

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
@Document(collectionName = "regions")
public class Region {
    private String regionId;
    private String eventorId;
    private String name;    
}
