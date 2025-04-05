package no.stunor.origo.eventorapi.model.person

import jakarta.persistence.Column

data class PersonName(
    @Column(name = "family_name") var family: String = "",
    @Column(name = "given_name")var given: String = ""
) {
    override fun toString(): String {
        return "$given $family"
    }
}