package no.stunor.origo.eventorapi.model.person

import com.fasterxml.jackson.annotation.JsonIgnore
import no.stunor.origo.eventorapi.model.organisation.Organisation
import java.io.Serializable
import java.util.*

data class MembershipKey(
    var personId: UUID? = UUID(0,0),
    var organisationId: UUID? = UUID(0,0),
) : Serializable

data class Membership(
    @JsonIgnore
    var id: MembershipKey = MembershipKey(),
    @JsonIgnore
    var person: Person? = null,
    var organisation: Organisation? = null,
    var type: MembershipType = MembershipType.Member
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