package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.Region
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RegionRepository : CrudRepository<Region, Int> {
    fun findByRegionIdAndEventorId(regionId: String,eventorId: String): Region?
}