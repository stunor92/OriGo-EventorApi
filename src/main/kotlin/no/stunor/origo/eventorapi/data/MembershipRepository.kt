package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.person.Membership
import no.stunor.origo.eventorapi.model.person.MembershipKey
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
interface MembershipRepository : CrudRepository<Membership, MembershipKey> {
    @Transactional
    @Modifying
    @Query("delete from Membership m where m.id.personId =  :personId")
    fun deleteByPersonId(personId: UUID?)
}
