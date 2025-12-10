package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.person.User
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class UserRepository(private val jdbcTemplate: JdbcTemplate) {
    
    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        User(id = rs.getString("id"))
    }
    
    fun findById(id: String): User? {
        return try {
            jdbcTemplate.queryForObject(
                "SELECT * FROM auth.users WHERE id = ?",
                rowMapper,
                id
            )
        } catch (e: Exception) {
            null
        }
    }
    
    fun save(user: User): User {
        jdbcTemplate.update(
            "INSERT INTO auth.users (id) VALUES (?) ON CONFLICT (id) DO NOTHING",
            user.id
        )
        return user
    }
    
    fun deleteById(id: String) {
        jdbcTemplate.update("DELETE FROM auth.users WHERE id = ?", id)
    }
}