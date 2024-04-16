package no.stunor.origo.eventorapi.model.origo.person;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompetitorPerson {
    String eventorId;
    String personid;
    PersonName name;
    Integer birthYear;
    String nationality;
    String gender;
}
