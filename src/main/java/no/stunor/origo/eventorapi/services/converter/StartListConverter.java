package no.stunor.origo.eventorapi.services.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.iof.eventor.ClassStart;
import org.iof.eventor.EventRace;
import org.iof.eventor.RaceStart;
import org.iof.eventor.StartList;
import org.iof.eventor.StartTime;

import no.stunor.origo.eventorapi.model.Eventor;
import no.stunor.origo.eventorapi.model.organisation.Organisation;
import no.stunor.origo.eventorapi.model.origo.start.PersonStart;
import no.stunor.origo.eventorapi.model.origo.start.RaceStartList;
import no.stunor.origo.eventorapi.model.origo.start.TeamMemberStart;
import no.stunor.origo.eventorapi.model.origo.start.TeamStart;

public class StartListConverter {
    public static List<RaceStartList> convertEventStartList(StartList startList, Eventor eventor){
        Map<String, RaceStartList> raceStartListMap = new HashMap<>();

        for (EventRace eventRace : startList.getEvent().getEventRace()){
            String raceId = eventRace.getEventRaceId().getContent();
            raceStartListMap.put(raceId, new RaceStartList(raceId, new ArrayList<>(), new ArrayList<>()));
        }

        for (ClassStart classStart : startList.getClassStart()) {
            for (Object personOrTeamStart : classStart.getPersonStartOrTeamStart()){
                if(personOrTeamStart instanceof org.iof.eventor.PersonStart){
                    if (((org.iof.eventor.PersonStart) personOrTeamStart).getRaceStart() != null && !((org.iof.eventor.PersonStart) personOrTeamStart).getRaceStart().isEmpty()){
                        for(RaceStart raceStart : ((org.iof.eventor.PersonStart) personOrTeamStart).getRaceStart()){
                            raceStartListMap
                                .get(raceStart.getEventRaceId().getContent())
                                .personStartList()
                                .add(convertMulitDayPersonStart(classStart, ((org.iof.eventor.PersonStart) personOrTeamStart), raceStart, eventor));
                        }
                    } else {
                        raceStartListMap
                            .get((startList.getEvent().getEventRace().get(0).getEventRaceId().getContent()))
                            .personStartList()
                            .add(convertOneDayPersonStart(classStart, ((org.iof.eventor.PersonStart) personOrTeamStart), eventor));
                    }
                } else if(personOrTeamStart instanceof org.iof.eventor.TeamStart){
                        raceStartListMap
                            .get((startList.getEvent().getEventRace().get(0).getEventRaceId().getContent()))
                            .teamStartList()
                            .add(convertTeamStart(classStart, ((org.iof.eventor.TeamStart) personOrTeamStart), eventor));
                } 
            }
        }

        return raceStartListMap.values().stream().toList();

    }

    private static PersonStart convertMulitDayPersonStart(ClassStart classStart, org.iof.eventor.PersonStart personStart, RaceStart raceStart, Eventor eventor) {  
        return new PersonStart(
            raceStart.getStart().getStartId().getContent(),
            PersonConverter.convertCompetitor(personStart.getPerson(), eventor),
            personStart.getOrganisation() != null && personStart.getOrganisation().getOrganisationId() != null ? OrganisationConverter.convertOrganisation(personStart.getOrganisation(), eventor) : null,
            raceStart.getStart().getStartTime() != null ? convertStartTime(raceStart.getStart().getStartTime()) : null,
            raceStart.getStart().getBibNumber() != null ? raceStart.getStart().getBibNumber().getContent() : "",
            classStart.getEventClass().getEventClassId().getContent());
    }

    private static PersonStart convertOneDayPersonStart(ClassStart classStart, org.iof.eventor.PersonStart personStart, Eventor eventor) {
        return new PersonStart(
            personStart.getStart().getStartId().getContent(),
            PersonConverter.convertCompetitor(personStart.getPerson(), eventor),
            personStart.getOrganisation() != null && personStart.getOrganisation().getOrganisationId() != null ? OrganisationConverter.convertOrganisation(personStart.getOrganisation(), eventor) : null,
            personStart.getStart().getStartTime() != null ? convertStartTime(personStart.getStart().getStartTime()) : null,
            personStart.getStart().getBibNumber() != null ? personStart.getStart().getBibNumber().getContent() : "",
            classStart.getEventClass().getEventClassId().getContent());
    }

    private static TeamStart convertTeamStart(ClassStart classStart, org.iof.eventor.TeamStart teamStart, Eventor eventor) {
        List<Organisation> organisations = new ArrayList<>();
        for (Object organisation : teamStart.getOrganisationIdOrOrganisationOrCountryId()) {
            organisations.add(OrganisationConverter.convertOrganisation((org.iof.eventor.Organisation) organisation, eventor));
        }
        return new TeamStart(
            "",
            organisations,
            convertTeamMembers(teamStart.getTeamMemberStart(), eventor),
            teamStart.getTeamName().getContent(),
            teamStart.getStartTime() != null ? convertStartTime(teamStart.getStartTime()) : null,
            teamStart.getBibNumber() != null ? teamStart.getBibNumber().getContent() : "",
            classStart.getEventClass().getEventClassId().getContent());

    }

     public static List<TeamMemberStart> convertTeamMembers(List<org.iof.eventor.TeamMemberStart> teamMembers, Eventor eventor) {
        List<TeamMemberStart> result = new ArrayList<>();
        for (org.iof.eventor.TeamMemberStart teamMember: teamMembers) {
            result.add(convertTeamMember(teamMember, eventor));
        }
        return result;
    }

    private static TeamMemberStart convertTeamMember(org.iof.eventor.TeamMemberStart teamMember, Eventor eventor) {
        return new TeamMemberStart(
            teamMember.getPerson() != null ? PersonConverter.convertCompetitor(teamMember.getPerson(), eventor) : null,
            teamMember.getLeg().intValue(),
            teamMember.getStartTime() != null ? convertStartTime(teamMember.getStartTime()) : null);
    }

    public static Date convertStartTime(StartTime startTime) {
        String dateString = startTime.getDate().getContent() + " " + startTime.getClock().getContent();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return parser.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
}