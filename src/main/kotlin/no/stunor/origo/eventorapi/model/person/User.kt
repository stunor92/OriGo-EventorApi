package no.stunor.origo.eventorapi.model.person

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users", schema = "auth") // Specify the schema explicitly
data class User(
    @Id val id: String = ""
)