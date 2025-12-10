package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.Eventor
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class EventorRepository(private val jdbcTemplate: JdbcTemplate) {
    
    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        Eventor(
            id = rs.getString("id"),
            name = rs.getString("name"),
            federation = rs.getString("federation"),
            baseUrl = rs.getString("base_url"),
            eventorApiKey = rs.getString("eventor_api_key")
        )
    }
    
    fun findById(id: String): Eventor? {
        return try {
            jdbcTemplate.queryForObject(
                "SELECT * FROM eventor WHERE id = ?",
                rowMapper,
                id
            )
        } catch (e: Exception) {
            null
        }
    }
    
    fun findAll(): List<Eventor> {
        return jdbcTemplate.query("SELECT * FROM eventor", rowMapper)
    }
    
    fun save(eventor: Eventor): Eventor {
        val count = jdbcTemplate.update(
            """
            INSERT INTO eventor (id, name, federation, base_url, eventor_api_key) 
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET 
                name = EXCLUDED.name,
                federation = EXCLUDED.federation,
                base_url = EXCLUDED.base_url,
                eventor_api_key = EXCLUDED.eventor_api_key
            """,
            eventor.id, eventor.name, eventor.federation, eventor.baseUrl, eventor.eventorApiKey
        )
        return eventor
    }
    
    fun deleteById(id: String) {
        jdbcTemplate.update("DELETE FROM eventor WHERE id = ?", id)
    }
}