package no.stunor.origo.eventorapi.model.person

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.io.Serializable

data class UserPersonId(
    private val userId: String,
    private val personId: String,
    private val eventorId: String
) : Serializable {
    constructor() : this("", "", "")
}

@Entity
@IdClass(UserPersonId::class)
data class UserPerson (
    @Id var userId: String = "",
    @Id var eventorId: String = "",
    @Id var personId: String = "",
    @ManyToOne
    @JoinColumns(
        JoinColumn(name = "personId", referencedColumnName = "personId", insertable = false, updatable = false),
        JoinColumn(name = "eventorId", referencedColumnName = "eventorId", insertable = false, updatable = false)
    )
    @JsonIgnore var person: Person? = null,
)