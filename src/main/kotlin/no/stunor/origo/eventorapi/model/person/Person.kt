package no.stunor.origo.eventorapi.model.person

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.io.Serializable
import java.time.ZonedDateTime

data class PersonId(
        private val personId: String,
        private val eventorId: String
) : Serializable {
        constructor() : this("", "")
}

@Entity
@IdClass(PersonId::class)
data class Person(
        @Id var eventorId: String = "",
        @Id var personId: String = "",
        @Embedded var name: PersonName = PersonName(),
        var birthYear: Int = 0,
        var nationality: String = "",
        @Enumerated(EnumType.STRING) var gender: Gender = Gender.Other,
        var mobilePhone: String? = null,
        var email: String? = null,
        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "person")
        var memberships: List<Membership> = mutableListOf(),
        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "person")
        @JsonIgnore var users: MutableList<UserPerson> = mutableListOf(),
        @JsonIgnore var lastUpdated: ZonedDateTime = ZonedDateTime.now()
)
