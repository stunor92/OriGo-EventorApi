package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.io.Serializable

data class DocumentId(
        private val documentId: String,
        private val eventId: String,
        private val eventorId: String
) : Serializable {
        constructor() : this("", "", "")
}

@Entity
@IdClass(DocumentId::class)
data class Document (
        @JsonIgnore @Id var eventorId: String = "",
        @JsonIgnore @Id var eventId: String = "",
        @Id var documentId: String = "",
        var name: String = "",
        var url: String = "",
        var type: String = "",
        @ManyToOne
        @JoinColumns(
                JoinColumn(name = "eventId", referencedColumnName = "eventId", insertable = true, updatable = true),
                JoinColumn(name = "eventorId", referencedColumnName = "eventorId", insertable = true, updatable = true)
        )
        @JsonIgnore var event: Event? = null,
)