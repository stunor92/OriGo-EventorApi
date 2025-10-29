package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.person.UserPerson
import no.stunor.origo.eventorapi.model.person.UserPersonKey
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface UserPersonRepository : CrudRepository<UserPerson, UserPersonKey> {
    @Transactional
    @Modifying
    @Query("delete from UserPerson up where up.id.userId = :userId and up.id.personId = :personId")
    fun deleteByUserIdAndPersonId(userId: String, personId: UUID?)
}