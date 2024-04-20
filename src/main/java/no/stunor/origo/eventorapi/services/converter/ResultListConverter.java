package no.stunor.origo.eventorapi.services.converter;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.iof.eventor.ClassResult;
import org.iof.eventor.EventRace;
import org.iof.eventor.FinishTime;
import org.iof.eventor.OverallResult;
import org.iof.eventor.RaceResult;
import org.iof.eventor.ResultList;
import org.iof.eventor.TeamMemberResult.Position;
import org.iof.eventor.TeamMemberResult.TimeBehind;

import no.stunor.origo.eventorapi.model.Eventor;
import no.stunor.origo.eventorapi.model.organisation.Organisation;
import no.stunor.origo.eventorapi.model.origo.result.PersonResult;
import no.stunor.origo.eventorapi.model.origo.result.RaceResultList;
import no.stunor.origo.eventorapi.model.origo.result.Result;
import no.stunor.origo.eventorapi.model.origo.result.SplitTime;
import no.stunor.origo.eventorapi.model.origo.result.TeamMemberResult;
import no.stunor.origo.eventorapi.model.origo.result.TeamResult;

public class ResultListConverter {

    public static List<RaceResultList> convertEventResultList(ResultList resultList, Eventor eventor) throws NumberFormatException, ParseException{
        Map<String, RaceResultList> raceResutlListMap = new HashMap<>();

        for (EventRace eventRace : resultList.getEvent().getEventRace()){
            String raceId = eventRace.getEventRaceId().getContent();
            raceResutlListMap.put(raceId, new RaceResultList(raceId, new ArrayList<>(), new ArrayList<>()));
        }

        for (ClassResult classResult : resultList.getClassResult()) {
            for (Object personOrTeamResult : classResult.getPersonResultOrTeamResult()){
                if(personOrTeamResult instanceof org.iof.eventor.PersonResult){
                    if (((org.iof.eventor.PersonResult) personOrTeamResult).getRaceResult() != null && !((org.iof.eventor.PersonResult) personOrTeamResult).getRaceResult().isEmpty()){
                        for(RaceResult raceResult : ((org.iof.eventor.PersonResult) personOrTeamResult).getRaceResult()){
                            raceResutlListMap
                                .get(raceResult.getEventRaceId().getContent())
                                .personResultList()
                                .add(convertMulitDayPersonResult(classResult, ((org.iof.eventor.PersonResult) personOrTeamResult), raceResult, eventor));
                        }
                    } else {
                        raceResutlListMap
                            .get(resultList.getEvent().getEventRace().get(0).getEventRaceId().getContent())
                            .personResultList()
                            .add(convertOneDayPersonResult(classResult, ((org.iof.eventor.PersonResult) personOrTeamResult), eventor));
                    }
                } else if(personOrTeamResult instanceof org.iof.eventor.TeamResult){
                        raceResutlListMap
                            .get(resultList.getEvent().getEventRace().get(0).getEventRaceId().getContent())
                            .teamResultList()
                            .add(convertTeamResult(classResult, ((org.iof.eventor.TeamResult) personOrTeamResult), eventor));
                } 
            }
        }

        return raceResutlListMap.values().stream().toList();

    }

    private static PersonResult convertOneDayPersonResult(ClassResult classResult, org.iof.eventor.PersonResult personResult, Eventor eventor) throws NumberFormatException, ParseException {
        return new PersonResult(
            personResult.getResult().getResultId().getContent(),
            PersonConverter.convertCompetitor(personResult.getPerson(), eventor),
            personResult.getOrganisation() != null && personResult.getOrganisation().getOrganisationId() != null? OrganisationConverter.convertOrganisation(personResult.getOrganisation(), eventor) : null,
            personResult.getResult().getStartTime() != null ? StartListConverter.convertStartTime(personResult.getResult().getStartTime()) : null,
            personResult.getResult().getFinishTime() != null ? convertFinishTime(personResult.getResult().getFinishTime()) : null,
            convertPersonResult(personResult.getResult()),
            convertSplitTimes(personResult.getResult().getSplitTime()),
            personResult.getResult().getBibNumber() != null ? personResult.getResult().getBibNumber().getContent() : "",
            classResult.getEventClass().getEventClassId().getContent());
    }

    private static PersonResult convertMulitDayPersonResult(ClassResult classResult, org.iof.eventor.PersonResult personResult, RaceResult raceResult, Eventor eventor) throws NumberFormatException, ParseException {
        return new PersonResult(
            raceResult.getEventRaceId().getContent(),
            PersonConverter.convertCompetitor(personResult.getPerson(), eventor),
            personResult.getOrganisation() != null && personResult.getOrganisation().getOrganisationId() != null? OrganisationConverter.convertOrganisation(personResult.getOrganisation(), eventor) : null,
            raceResult.getResult().getStartTime() != null ? StartListConverter.convertStartTime(raceResult.getResult().getStartTime()) : null,
            raceResult.getResult().getFinishTime() != null ? convertFinishTime(raceResult.getResult().getFinishTime()) : null,
            convertPersonResult(raceResult.getResult()),
            convertSplitTimes(raceResult.getResult().getSplitTime()),
            raceResult.getResult().getBibNumber() != null ? raceResult.getResult().getBibNumber().getContent() : "",
            classResult.getEventClass().getEventClassId().getContent());
    }

    public static Result convertPersonResult(org.iof.eventor.Result result) throws NumberFormatException, ParseException {
        return new Result(
            result.getTime() != null ? convertTimetoSec(result.getTime().getContent()) : null,
            result.getTimeDiff() != null ?  convertTimetoSec(result.getTimeDiff().getContent()) : null,
            result.getResultPosition() != null && !result.getResultPosition().getContent().equals("0") ? Integer.parseInt(result.getResultPosition().getContent()) : null ,
            result.getCompetitorStatus().getValue());
    }

    private static TeamResult convertTeamResult(ClassResult classResult, org.iof.eventor.TeamResult teamResult, Eventor eventor) throws NumberFormatException, ParseException {
         List<Organisation> organisations = new ArrayList<>();
        for (Object organisation : teamResult.getOrganisationIdOrOrganisationOrCountryId()) {
            organisations.add(OrganisationConverter.convertOrganisation((org.iof.eventor.Organisation) organisation, eventor));
        }
        return new TeamResult(
            "",
            organisations,
            convertTeamMembers(teamResult.getTeamMemberResult(), eventor),
            teamResult.getTeamName().getContent(),
            teamResult.getStartTime() != null ? StartListConverter.convertStartTime(teamResult.getStartTime()) : null,
            teamResult.getFinishTime() != null ? convertFinishTime(teamResult.getFinishTime()) : null,
            convertTeamResult(teamResult),
            teamResult.getBibNumber() != null ? teamResult.getBibNumber().getContent() : null,
            classResult.getEventClass().getEventClassId().getContent());
    }

    private static List<SplitTime> convertSplitTimes(List<org.iof.eventor.SplitTime> splitTimes) throws NumberFormatException, ParseException {
        List<SplitTime> result = new ArrayList<>();
        for (org.iof.eventor.SplitTime splitTime: splitTimes) {
            result.add(convertSplitTime(splitTime));
        }
        return result;

    }

    private static SplitTime convertSplitTime(org.iof.eventor.SplitTime splitTime) throws NumberFormatException, ParseException {
        return new SplitTime(
            Integer.parseInt(splitTime.getSequence()), 
            splitTime.getControlCode().getContent(), 
            splitTime.getTime() != null ? convertTimetoSec(splitTime.getTime().getContent()) : null);
    }

    public static List<TeamMemberResult> convertTeamMembers(List<org.iof.eventor.TeamMemberResult> teamMembers, Eventor eventor) throws ParseException {
        List<TeamMemberResult> result = new ArrayList<>();
        for (org.iof.eventor.TeamMemberResult teamMember: teamMembers) {
            result.add(convertTeamMember(teamMember, eventor));
        }
        return result;
    }

    private static TeamMemberResult convertTeamMember(org.iof.eventor.TeamMemberResult teamMember, Eventor eventor) throws ParseException {
        return new TeamMemberResult(
            teamMember.getPerson() != null ? PersonConverter.convertCompetitor(teamMember.getPerson(), eventor) : null,
            teamMember.getLeg().intValue(),
            teamMember.getStartTime() != null ? StartListConverter.convertStartTime(teamMember.getStartTime()) : null,
            teamMember.getFinishTime() != null ? convertFinishTime(teamMember.getFinishTime()) : null,
            convertLegResult(teamMember),
            teamMember.getOverallResult() != null ? convertOverallResult(teamMember.getOverallResult()) : null,
            convertSplitTimes(teamMember.getSplitTime()));
    }
    
    public static Result convertTeamResult(org.iof.eventor.TeamResult teamResult) throws NumberFormatException, ParseException {
        return new Result(
            teamResult.getTime() != null ? convertTimetoSec(teamResult.getTime().getContent()) : null,
            teamResult.getTimeDiff() != null ? convertTimetoSec(teamResult.getTimeDiff().getContent()) : null,
            teamResult.getResultPosition() != null && !teamResult.getResultPosition().getContent().equals("0") ? Integer.parseInt(teamResult.getResultPosition().getContent()) : null,
            teamResult.getTeamStatus().getValue());
    }

    private static Result convertOverallResult(OverallResult overallResult) throws NumberFormatException, ParseException {
        return new Result(
            overallResult.getTime() != null ? convertTimetoSec(overallResult.getTime().getContent()) : null,
            overallResult.getTimeDiff() != null ? convertTimetoSec(overallResult.getTimeDiff().getContent()) : null,
            overallResult.getResultPosition() != null && !overallResult.getResultPosition().getContent().equals("0") ? Integer.parseInt(overallResult.getResultPosition().getContent()) : null,
            overallResult.getTeamStatus().getValue());
    }

    private static Result convertLegResult(org.iof.eventor.TeamMemberResult teamMember) throws ParseException {
        return new Result(
            teamMember.getTime() != null ? convertTimetoSec(teamMember.getTime().getContent()) : null,
            teamMember.getTimeBehind() != null ? getTimeBehind(teamMember.getTimeBehind()) : null,
            teamMember.getPosition() != null ? getLegPosition(teamMember.getPosition()) : null,
            teamMember.getCompetitorStatus().getValue());
    }

    private static Integer getLegPosition(List<Position> positionList) {
        for(Position position : positionList){
            if(position.getType().equals("Leg") && position.getValue().intValue() > 0){
                return position.getValue().intValue();
            }
        }
        return null;
    }

    public static Integer convertTimetoSec(String time) throws ParseException {
        Date date;
        Date reference;
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            reference = dateFormat.parse("00:00:00");
            date = dateFormat.parse(time);
        } catch (ParseException e) {
            DateFormat dateFormat = new SimpleDateFormat("mm:ss");
            reference = dateFormat.parse("00:00");
            date = dateFormat.parse(time);
        }
        long seconds = (date.getTime() - reference.getTime()) / 1000L;
        return (int) seconds;
    }

    

    private static Integer getTimeBehind(List<TimeBehind> timeBehindList) {
        for(TimeBehind timeBehind : timeBehindList){
            if(timeBehind.getType().equals("Leg")){
                return (int) timeBehind.getValue();
            }
        }
        return null;
    }

    public static Date convertFinishTime(FinishTime finishTime) {
        String dateString = finishTime.getDate().getContent() + " " + finishTime.getClock().getContent();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return parser.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
}
