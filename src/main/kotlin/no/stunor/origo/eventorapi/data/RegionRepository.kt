package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.Region
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RegionRepository : CrudRepository<Region, String> {
    fun findByEventorRefAndEventorId(eventorRef: String, eventorId: String): Region?
}