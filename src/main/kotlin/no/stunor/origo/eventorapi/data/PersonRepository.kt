package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.person.Person
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : CrudRepository<Person, String> {
    fun findByEventorIdAndEventorRef(eventorId: String, eventorRef: String): Person?
    @Query("SELECT p FROM Person p JOIN p.users u WHERE CAST(u.id.userId AS String) LIKE %:userId%")
    fun findAllByUsers(@Param("userId") userId: String): List<Person>

    @Query("SELECT p FROM Person p JOIN p.users u WHERE CAST(u.id.userId AS String) LIKE %:userId% AND p.eventorId = :eventorId")
    fun findAllByUsersAndEventorId(@Param("userId") userId: String, @Param("eventorId") eventorId: String): List<Person>
}