package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.Eventor
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EventorRepository : CrudRepository<Eventor, String>