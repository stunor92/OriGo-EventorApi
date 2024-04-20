package no.stunor.origo.eventorapi.data

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository
import no.stunor.origo.eventorapi.model.Region
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface RegionRepository : FirestoreReactiveRepository<Region> {
    fun findByRegionIdAndEventorId(regionId: String, eventorId: String): Mono<Region>
}