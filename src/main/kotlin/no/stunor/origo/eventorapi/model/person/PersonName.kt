package no.stunor.origo.eventorapi.model.person

data class PersonName(
    var family: String = "",
    var given: String = ""
) {
    override fun toString(): String {
        return "$given $family"
    }
}