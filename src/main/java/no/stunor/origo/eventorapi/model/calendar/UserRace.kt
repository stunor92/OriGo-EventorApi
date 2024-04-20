package no.stunor.origo.eventorapi.model.calendar;

import no.stunor.origo.eventorapi.model.Eventor
import java.util.Date;

import no.stunor.origo.eventorapi.model.origo.entry.EntryBreak;
import java.io.Serializable

data class UserRace(
        var eventor: Eventor = Eventor(),
        var eventId: String = "",
        var eventName: String = "",
        var raceId: String = "",
        var raceName: String? = null,
        var canceled: Boolean = false,
        var raceDate: Date? = Date(),
        var userCompetitors: MutableList<UserCompetitor> = mutableListOf(),
        var organisationEntries: MutableMap<String, Int> = mutableMapOf(),
        var entryBreaks: List<EntryBreak> = ArrayList()
) : Serializable