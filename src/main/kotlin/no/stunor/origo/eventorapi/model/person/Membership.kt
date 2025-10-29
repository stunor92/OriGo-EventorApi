package no.stunor.origo.eventorapi.model.person

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import no.stunor.origo.eventorapi.model.organisation.Organisation
import java.io.Serializable
import java.util.*

@Embeddable
data class MembershipKey(
    @Column(name = "person_id") var personId: UUID? = UUID(0,0),
    @Column(name = "organisation_id") var organisationId: UUID? = UUID(0,0),
) : Serializable

@Entity
@Table(name = "membership")
data class Membership(
    @JsonIgnore
    @EmbeddedId
    var id: MembershipKey = MembershipKey(),
    @JsonIgnore
    @MapsId("personId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    var person: Person? = null,
    @MapsId("organisationId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    var organisation: Organisation? = null,
    @Enumerated(EnumType.STRING) var type: MembershipType = MembershipType.Member
) {
    var personId: UUID?
        get() = id.personId
        set(value) { id.personId = value }

    var organisationId: UUID?
        get() = id.organisationId
        set(value) { id.organisationId = value }
}

enum class MembershipType  {
    Member, Organiser, Admin
}