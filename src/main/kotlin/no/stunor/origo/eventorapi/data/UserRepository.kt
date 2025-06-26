package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.person.User
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepository : CrudRepository<User, String> {
    @Transactional
    @Modifying
    override fun deleteById(id: String)
}