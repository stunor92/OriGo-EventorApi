package no.stunor.origo.eventorapi.model.person

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.cloud.firestore.annotation.DocumentId


data class Person(
        @JsonIgnore
        @DocumentId
        var id: String? = null,
        var eventorId: String = "",
        var personId: String = "",
        var name: PersonName = PersonName(),
        var birthYear: Int = 0,
        var nationality: String = "",
        var gender: Gender = Gender.Other,
        @JsonIgnore
        var users: MutableList<String> = mutableListOf(),
        var mobilePhone: String? = null,
        var email: String? = null,
        var memberships: Map<String, MembershipType> = HashMap(),
)
