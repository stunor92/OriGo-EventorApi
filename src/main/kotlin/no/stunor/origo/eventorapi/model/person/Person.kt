package no.stunor.origo.eventorapi.model.person

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

@Entity
data class Person(
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    var id: UUID? = null,
    var eventorId: String = "",
    var eventorRef: String = "",
    @Embedded var name: PersonName = PersonName(),
    var birthYear: Int = 0,
    var nationality: String = "",
    @Enumerated(EnumType.STRING) var gender: Gender = Gender.Other,
    var mobilePhone: String? = null,
    var email: String? = null,
    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
    var memberships: MutableList<Membership> = mutableListOf(),
    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore var users: MutableList<UserPerson> = mutableListOf(),
    @JsonIgnore var lastUpdated: Instant = Instant.now()
)
