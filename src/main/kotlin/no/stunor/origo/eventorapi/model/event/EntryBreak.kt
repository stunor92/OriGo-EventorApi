package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.io.Serializable
import java.time.Instant

data class EntryBreakId(
        private val eventorId: String,
        private val eventId: String,
        private val entryBreakId: Int,
        ) : Serializable {
        constructor() : this("", "", 0)
}

@Entity
@IdClass(EntryBreakId::class)
data class EntryBreak (
        @JsonIgnore @Id var eventorId: String = "",
        @JsonIgnore @Id var eventId: String = "",
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var entryBreakId: Int = 0,
        @Column(name = "from_date") var from: Instant? = null,
        @Column(name = "to_date") var to: Instant? = null,
        @ManyToOne
        @JoinColumns(
                JoinColumn(name = "eventId", referencedColumnName = "eventId", insertable = false, updatable = false),
                JoinColumn(name = "eventorId", referencedColumnName = "eventorId", insertable = false, updatable = false)
        )
        @JsonIgnore var event: Event? = null,
)