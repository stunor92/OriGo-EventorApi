package no.stunor.origo.eventorapi.model.origo.person;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonName {
    String family;
    String given;

    @Override
    public String toString() {
        return given + " " + family;
    }
}
