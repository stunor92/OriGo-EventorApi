package no.stunor.origo.eventorapi.model.person

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant
import java.util.*

data class Person(
    var id: UUID? = null,
    var eventorId: String = "",
    var eventorRef: String = "",
    var name: PersonName = PersonName(),
    var birthYear: Int = 0,
    var nationality: String = "",
    var gender: Gender = Gender.Other,
    var mobilePhone: String? = null,
    var email: String? = null,
    var memberships: MutableList<Membership> = mutableListOf(),
    @JsonIgnore var users: MutableList<UserPerson> = mutableListOf(),
    @JsonIgnore var lastUpdated: Instant = Instant.now()
)
