package no.stunor.origo.eventorapi.model.person

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.util.*

data class UserPersonKey(
    var userId: UUID? = null,
    var personId: UUID? = null,
) : Serializable

data class UserPerson(
    var id: UserPersonKey = UserPersonKey(),
    @JsonIgnore var person: Person? = null,
) {
    var userId: UUID?
        get() = id.userId
        set(value) { id.userId = value }
    var personId: UUID?
        get() = id.personId
        set(value) { id.personId = value }
}
