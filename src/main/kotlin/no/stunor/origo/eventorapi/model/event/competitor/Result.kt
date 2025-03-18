package no.stunor.origo.eventorapi.model.event.competitor

import com.google.firebase.database.Exclude


data class Result(
        var time: Int? = null,
        @Exclude
        var timeBehind: Int? =  null,
        @Exclude
        val position: Int? = null,
        val status: ResultStatus = ResultStatus.OK
)
