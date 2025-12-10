package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.Person
import no.stunor.origo.eventorapi.model.person.PersonName
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.Instant
import java.util.*

@Repository
class PersonRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val membershipRepository: MembershipRepository,
    private val userPersonRepository: UserPersonRepository
) {
    
    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        val id = rs.getObject("id", UUID::class.java)
        Person(
            id = id,
            eventorId = rs.getString("eventor_id"),
            eventorRef = rs.getString("eventor_ref"),
            name = PersonName(
                family = rs.getString("family_name") ?: "",
                given = rs.getString("given_name") ?: ""
            ),
            birthYear = rs.getInt("birth_year"),
            nationality = rs.getString("nationality") ?: "",
            gender = Gender.valueOf(rs.getString("gender") ?: "Other"),
            mobilePhone = rs.getString("mobile_phone"),
            email = rs.getString("email"),
            memberships = membershipRepository.findAllByPersonId(id).toMutableList(),
            users = userPersonRepository.findAllByUserId(rs.getString("eventor_id")).toMutableList(),
            lastUpdated = rs.getTimestamp("last_updated")?.toInstant() ?: Instant.now()
        )
    }
    
    fun findByEventorIdAndEventorRef(eventorId: String, eventorRef: String): Person? {
        return try {
            jdbcTemplate.queryForObject(
                "SELECT * FROM person WHERE eventor_id = ? AND eventor_ref = ?",
                rowMapper,
                eventorId, eventorRef
            )
        } catch (e: Exception) {
            null
        }
    }
    
    fun findAllByUsers(userId: String): List<Person> {
        return jdbcTemplate.query(
            """
            SELECT p.* FROM person p
            INNER JOIN user_person up ON p.id = up.person_id
            WHERE up.user_id LIKE ?
            """,
            rowMapper,
            "%$userId%"
        )
    }
    
    fun findAllByUsersAndEventorId(userId: String, eventorId: String): List<Person> {
        return jdbcTemplate.query(
            """
            SELECT p.* FROM person p
            INNER JOIN user_person up ON p.id = up.person_id
            WHERE up.user_id LIKE ? AND p.eventor_id = ?
            """,
            rowMapper,
            "%$userId%", eventorId
        )
    }
    
    fun save(person: Person): Person {
        if (person.id == null) {
            person.id = UUID.randomUUID()
            jdbcTemplate.update(
                """
                INSERT INTO person (id, eventor_id, eventor_ref, family_name, given_name, 
                    birth_year, nationality, gender, mobile_phone, email, last_updated)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                person.id, person.eventorId, person.eventorRef, person.name.family, person.name.given,
                person.birthYear, person.nationality, person.gender.name, person.mobilePhone, 
                person.email, person.lastUpdated
            )
        } else {
            jdbcTemplate.update(
                """
                INSERT INTO person (id, eventor_id, eventor_ref, family_name, given_name, 
                    birth_year, nationality, gender, mobile_phone, email, last_updated)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    eventor_id = EXCLUDED.eventor_id,
                    eventor_ref = EXCLUDED.eventor_ref,
                    family_name = EXCLUDED.family_name,
                    given_name = EXCLUDED.given_name,
                    birth_year = EXCLUDED.birth_year,
                    nationality = EXCLUDED.nationality,
                    gender = EXCLUDED.gender,
                    mobile_phone = EXCLUDED.mobile_phone,
                    email = EXCLUDED.email,
                    last_updated = EXCLUDED.last_updated
                """,
                person.id, person.eventorId, person.eventorRef, person.name.family, person.name.given,
                person.birthYear, person.nationality, person.gender.name, person.mobilePhone, 
                person.email, person.lastUpdated
            )
        }
        
        // Save memberships
        person.memberships.forEach { membership ->
            membership.person = person
            membership.id.personId = person.id
            membershipRepository.save(membership)
        }
        
        // Save user associations
        person.users.forEach { userPerson ->
            userPerson.person = person
            userPerson.id.personId = person.id
            userPersonRepository.save(userPerson)
        }
        
        return person
    }
}