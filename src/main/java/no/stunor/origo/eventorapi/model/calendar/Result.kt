package no.stunor.origo.eventorapi.model.calendar

import java.io.Serializable

data class Result(
        var time: Int? = null,
        var timeBehind: Int? =  null,
        val position: Int? = null,
        val status: String = "OK"
) : Serializable