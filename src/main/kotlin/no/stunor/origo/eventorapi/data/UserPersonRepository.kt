package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.person.UserPerson
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserPersonRepository : CrudRepository<UserPerson, String> {
    @Transactional
    @Modifying
    fun deleteByUserIdAndPersonIdAndEventorId(userId: String,personId: String, eventorId: String)
}