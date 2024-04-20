package no.stunor.origo.eventorapi.services.converter;


import java.util.ArrayList;
import java.util.List;

import no.stunor.origo.eventorapi.model.organisation.OrganisationType;
import org.iof.eventor.OrganisationId;

import no.stunor.origo.eventorapi.model.Eventor;
import no.stunor.origo.eventorapi.model.organisation.Organisation;

public class OrganisationConverter {

    public static List<String> convertOrganisations(List<org.iof.eventor.Organisation> organisations) {
        List<String> result = new ArrayList<>();
        for (org.iof.eventor.Organisation organisation : organisations) {
            if(organisation != null) {
                result.add(organisation.getOrganisationId().getContent());
            }

        }
        return result;
    }

    public static List<String> convertOrganisationsObject(List<Object> organisations) {
        List<String> result = new ArrayList<>();
        for (Object organisation : organisations) {
            if(organisation != null) {
                result.add(((org.iof.eventor.Organisation) organisation).getOrganisationId().getContent());
            }

        }
        return result;
    }

    public static List<String> convertOrganisationIds(List<OrganisationId> organisationIds) {
        List<String> result = new ArrayList<>();
        for (OrganisationId organisationId : organisationIds) {
            if(organisationId != null && organisationId.getContent() != null){
                result.add(organisationId.getContent());
            }

        }
        return result;
    }

    public static Organisation convertOrganisation(org.iof.eventor.Organisation organisation, Eventor eventor) {
        return new Organisation(
            organisation.getOrganisationId().getContent(),
            eventor.getEventorId(),
            organisation.getName().getContent(),
            convertOrganisationType(organisation),
            organisation.getCountry() != null ? organisation.getCountry().getAlpha3().getValue() : null,
            null,
            null,
            null,
            null);
    }


    public static OrganisationType convertOrganisationType(org.iof.eventor.Organisation organisation){

        return switch (organisation.getOrganisationTypeId().getContent()) {
            case "1" -> OrganisationType.FEDERATION;
            case "2" -> OrganisationType.REGION;
            case "5" -> OrganisationType.IOF;
            default -> OrganisationType.CLUB;
        };
    }
}
