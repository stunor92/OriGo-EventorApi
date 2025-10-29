package no.stunor.origo.eventorapi.model.person

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Embeddable
data class UserPersonKey(
    var userId: String = "",
    @Column(name = "person_id") var personId: UUID? = null,
) : Serializable

@Entity
@Table(name = "user_person")
data class UserPerson(
    @EmbeddedId var id: UserPersonKey = UserPersonKey(),
    @MapsId("personId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    @JsonIgnore var person: Person? = null,
) {
    var userId: String
        get() = id.userId
        set(value) { id.userId = value }
    var personId: UUID?
        get() = id.personId
        set(value) { id.personId = value }
}
