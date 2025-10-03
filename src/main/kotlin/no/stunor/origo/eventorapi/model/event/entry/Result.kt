package no.stunor.origo.eventorapi.model.event.entry

import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

data class Result(
        @Column(insertable=false, updatable=false) var time: Int? = null,
        @Column(insertable=false, updatable=false) var timeBehind: Int? =  null,
        @Column(insertable=false, updatable=false) val position: Int? = null,
        @Column(name = "result_status", insertable = false, updatable = false) @Enumerated(EnumType.STRING) val status: ResultStatus = ResultStatus.OK
)
