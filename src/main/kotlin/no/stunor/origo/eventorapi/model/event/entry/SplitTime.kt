package no.stunor.origo.eventorapi.model.event.entry

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import java.io.Serializable


data class SplitTimeId(
        private val entryId: String,
        private val leg: Int?,
        private val sequence: Int
) : Serializable {
        constructor() : this("", null, 0)
}

@Entity
@IdClass(SplitTimeId::class)
data class SplitTime(
        @JsonIgnore @Id val entryId: String = "",
        @JsonIgnore @Id val leg: Int? = null,
        @Id val sequence: Int = 0,
        val controlCode: String = "",
        val time: Int? = null
)
