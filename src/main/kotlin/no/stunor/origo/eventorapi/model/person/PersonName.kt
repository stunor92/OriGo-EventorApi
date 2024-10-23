package no.stunor.origo.eventorapi.model.person

import java.io.Serializable

data class PersonName(
    var family: String = "",
    var given: String = ""
) : Serializable {
    override fun toString(): String {
        return "$given $family"
    }
}
