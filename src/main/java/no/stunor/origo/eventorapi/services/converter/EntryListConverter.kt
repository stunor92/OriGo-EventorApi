package no.stunor.origo.eventorapi.services.converter;

import java.util.ArrayList;
import java.util.List;

import org.iof.eventor.Entry;
import org.iof.eventor.EntryEntryFee;
import org.iof.eventor.EntryList;
import org.iof.eventor.TeamCompetitor;

import no.stunor.origo.eventorapi.model.Eventor;
import no.stunor.origo.eventorapi.model.organisation.Organisation;
import no.stunor.origo.eventorapi.model.origo.entry.EventEntryList;
import no.stunor.origo.eventorapi.model.origo.entry.PersonEntry;
import no.stunor.origo.eventorapi.model.origo.entry.TeamEntry;
import no.stunor.origo.eventorapi.model.origo.entry.TeamMemberEntry;

public class EntryListConverter {
    public static EventEntryList convertEventEntryList(EntryList entryList, Eventor eventor) {
        List<PersonEntry> personEntries = new ArrayList<>();
        List<TeamEntry> teamEntries = new ArrayList<>();

        for (Entry entry : entryList.getEntry()){
            if(entry.getCompetitor() != null){
                personEntries.add(convertPersonEntry(entry, eventor));
            } else if (entry.getTeamCompetitor() != null){
                teamEntries.add(convertTeamEntry(entry, eventor));
            }
        }
        return new EventEntryList(personEntries, teamEntries);
    }

    private static PersonEntry convertPersonEntry(Entry entry, Eventor eventor) {
        return new PersonEntry(
            entry.getEntryId().getContent(),
            PersonConverter.convertCompetitor(entry.getCompetitor().getPerson(), eventor),
            entry.getCompetitor().getOrganisation() != null && entry.getCompetitor().getOrganisation().getOrganisationId() != null? OrganisationConverter.convertOrganisation(entry.getCompetitor().getOrganisation(), eventor) : null,
            entry.getCompetitor().getCCard() != null && !entry.getCompetitor().getCCard().isEmpty() ? EventConverter.convertCCard(entry.getCompetitor().getCCard().get(0)) : null,
            entry.getBibNumber() != null ? entry.getBibNumber().getContent() : "",
            entry.getEventRaceId() != null ? EventConverter.convertEventRaceIds(entry.getEventRaceId()) : new ArrayList<>(),
            entry.getEntryEntryFee() != null ? convertEntryFees(entry.getEntryEntryFee()) : new ArrayList<>(),
            entry.getEntryClass() != null && !entry.getEntryClass().isEmpty() ? EventClassConverter.convertEventClassId(entry.getEntryClass().get(0)) : null
        );
    }


  

    private static TeamEntry convertTeamEntry(Entry entry, Eventor eventor) {
        return new TeamEntry(
            entry.getEntryId().getContent(),
            convertTeamOrganisations(entry.getTeamCompetitor(), eventor),
            convertTeamMembers(entry.getTeamCompetitor(), eventor),
            entry.getTeamName().getContent(),
            entry.getBibNumber() != null ? entry.getBibNumber().getContent() : "",
            entry.getEntryEntryFee() != null ? convertEntryFees(entry.getEntryEntryFee()) : new ArrayList<>(),
            entry.getEntryClass() != null ? EventClassConverter.convertEventClassIds(entry.getEntryClass()) : new ArrayList<>()
        );
    }

    private static List<Organisation> convertTeamOrganisations(List<TeamCompetitor> teamCompetitors, Eventor eventor) {
        List<Organisation> result = new ArrayList<>();
        for (TeamCompetitor teamCompetitor : teamCompetitors) {
            if(teamCompetitor.getOrganisation() != null) {
                boolean organisationExist = false;
                for(Organisation organisation : result){
                    if(organisation.getOrganisationId().equals(teamCompetitor.getOrganisation().getOrganisationId().getContent())){
                        organisationExist = true;
                    }
                }
                if(!organisationExist){
                    result.add(OrganisationConverter.convertOrganisation(teamCompetitor.getOrganisation(), eventor));
                }
            }

        }
        return result;
    }

    public static List<TeamMemberEntry> convertTeamMembers(List<TeamCompetitor> teamMembers, Eventor eventor) {
        List<TeamMemberEntry> result = new ArrayList<>();
        for (TeamCompetitor teamMember: teamMembers) {
            result.add(convertTeamMember(teamMember, eventor));
        }
        return result;
    }

    private static List<String> convertEntryFees(List<EntryEntryFee> entryFees) {
        List<String> result = new ArrayList<>();
        for (EntryEntryFee entryFee: entryFees) {
            result.add(entryFee.getEntryFeeId().getContent());
        }
        return result;
    }

    private static TeamMemberEntry convertTeamMember(TeamCompetitor teamMember, Eventor eventor) {
        return new TeamMemberEntry(
            teamMember.getPerson() != null ? PersonConverter.convertCompetitor(teamMember.getPerson(), eventor) : null,
            Integer.parseInt(teamMember.getTeamSequence().getContent()),
            teamMember.getCCard() != null  && !teamMember.getCCard().isEmpty()? EventConverter.convertCCard(teamMember.getCCard().get(0)) : null
        );
    }
}
