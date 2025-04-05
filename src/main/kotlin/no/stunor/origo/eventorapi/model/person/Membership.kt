package no.stunor.origo.eventorapi.model.person

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.io.Serializable

data class MembershipId(
    private val personId: String,
    private val organisationId: String,
    private val eventorId: String
) : Serializable {
    constructor() : this("", "", "")
}

@Entity
@IdClass(MembershipId::class)
data class Membership (
    @JsonIgnore @Id var eventorId: String = "",
    @JsonIgnore@Id var personId: String = "",
    @Id var organisationId: String = "",
    @Enumerated(EnumType.STRING) var type: MembershipType = MembershipType.Member,
    @ManyToOne
    @JoinColumns(
        JoinColumn(name = "personId", referencedColumnName = "personId", insertable = false, updatable = false),
        JoinColumn(name = "eventorId", referencedColumnName = "eventorId", insertable = false, updatable = false)
    )
    @JsonIgnore var person: Person? = null,
)

enum class MembershipType  {
    Member, Organiser, Admin
}