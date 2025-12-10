package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.event.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.Array
import java.sql.ResultSet
import java.util.*

@Repository
class EventRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val organisationRepository: OrganisationRepository
) {
    
    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        val id = rs.getObject("id", UUID::class.java)
        
        // Parse array columns
        val disciplinesArray = rs.getArray("disciplines")
        val disciplines = if (disciplinesArray != null) {
            (disciplinesArray.array as kotlin.Array<*>).map { Discipline.valueOf(it.toString()) }.toTypedArray()
        } else {
            emptyArray()
        }
        
        val punchingTypesArray = rs.getArray("punching_unit_types")
        val punchingUnitTypes = if (punchingTypesArray != null) {
            (punchingTypesArray.array as kotlin.Array<*>).map { PunchingUnitType.valueOf(it.toString()) }.toTypedArray()
        } else {
            emptyArray()
        }
        
        val entryBreaksArray = rs.getArray("entry_breaks")
        val entryBreaks = if (entryBreaksArray != null) {
            (entryBreaksArray.array as kotlin.Array<*>).map { java.sql.Timestamp(it as Long) }.toTypedArray()
        } else {
            emptyArray()
        }
        
        val webUrlsArray = rs.getArray("web_urls")
        val webUrls = if (webUrlsArray != null) {
            (webUrlsArray.array as kotlin.Array<*>).map { it.toString() }
        } else {
            emptyList()
        }
        
        Event(
            id = id,
            eventorId = rs.getString("eventor_id"),
            eventorRef = rs.getString("eventor_ref"),
            name = rs.getString("name"),
            type = EventFormEnum.valueOf(rs.getString("type")),
            classification = EventClassificationEnum.valueOf(rs.getString("classification")),
            status = EventStatusEnum.valueOf(rs.getString("status")),
            disciplines = disciplines,
            punchingUnitTypes = punchingUnitTypes,
            startDate = rs.getTimestamp("start_date"),
            finishDate = rs.getTimestamp("finish_date"),
            organisers = mutableListOf(), // Load separately
            classes = mutableListOf(), // Load separately
            documents = mutableListOf(), // Load separately
            entryBreaks = entryBreaks,
            races = mutableListOf(), // Load separately
            webUrls = webUrls,
            message = rs.getString("message"),
            email = rs.getString("email"),
            phone = rs.getString("phone")
        )
    }
    
    fun findByEventorIdAndEventorRef(eventorId: String, eventorRef: String): Event? {
        return try {
            jdbcTemplate.queryForObject(
                "SELECT * FROM event WHERE eventor_id = ? AND eventor_ref = ?",
                rowMapper,
                eventorId, eventorRef
            )
        } catch (e: Exception) {
            null
        }
    }
    
    fun save(event: Event): Event {
        // Convert arrays to SQL arrays
        val disciplinesArray = jdbcTemplate.dataSource?.connection?.use { conn ->
            conn.createArrayOf("varchar", event.disciplines.map { it.name }.toTypedArray())
        }
        
        val punchingTypesArray = jdbcTemplate.dataSource?.connection?.use { conn ->
            conn.createArrayOf("varchar", event.punchingUnitTypes.map { it.name }.toTypedArray())
        }
        
        val entryBreaksArray = jdbcTemplate.dataSource?.connection?.use { conn ->
            conn.createArrayOf("timestamp", event.entryBreaks)
        }
        
        val webUrlsArray = jdbcTemplate.dataSource?.connection?.use { conn ->
            conn.createArrayOf("varchar", event.webUrls.toTypedArray())
        }
        
        if (event.id == null) {
            event.id = UUID.randomUUID()
            jdbcTemplate.update(
                """
                INSERT INTO event (id, eventor_id, eventor_ref, name, type, classification, status,
                    disciplines, punching_unit_types, start_date, finish_date, entry_breaks, web_urls,
                    message, email, phone)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                event.id, event.eventorId, event.eventorRef, event.name, event.type.name,
                event.classification.name, event.status.name, disciplinesArray, punchingTypesArray,
                event.startDate, event.finishDate, entryBreaksArray, webUrlsArray,
                event.message, event.email, event.phone
            )
        } else {
            jdbcTemplate.update(
                """
                INSERT INTO event (id, eventor_id, eventor_ref, name, type, classification, status,
                    disciplines, punching_unit_types, start_date, finish_date, entry_breaks, web_urls,
                    message, email, phone)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    eventor_id = EXCLUDED.eventor_id,
                    eventor_ref = EXCLUDED.eventor_ref,
                    name = EXCLUDED.name,
                    type = EXCLUDED.type,
                    classification = EXCLUDED.classification,
                    status = EXCLUDED.status,
                    disciplines = EXCLUDED.disciplines,
                    punching_unit_types = EXCLUDED.punching_unit_types,
                    start_date = EXCLUDED.start_date,
                    finish_date = EXCLUDED.finish_date,
                    entry_breaks = EXCLUDED.entry_breaks,
                    web_urls = EXCLUDED.web_urls,
                    message = EXCLUDED.message,
                    email = EXCLUDED.email,
                    phone = EXCLUDED.phone
                """,
                event.id, event.eventorId, event.eventorRef, event.name, event.type.name,
                event.classification.name, event.status.name, disciplinesArray, punchingTypesArray,
                event.startDate, event.finishDate, entryBreaksArray, webUrlsArray,
                event.message, event.email, event.phone
            )
        }
        
        // Save organisers (many-to-many)
        jdbcTemplate.update("DELETE FROM event_organiser WHERE event_id = ?", event.id)
        event.organisers.forEach { org ->
            org.id?.let { orgId ->
                organisationRepository.save(org)
                jdbcTemplate.update(
                    "INSERT INTO event_organiser (event_id, organisation_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                    event.id, orgId
                )
            }
        }
        
        // Save classes, documents, and races would go here
        // For now, simplified to get compilation working
        
        return event
    }
}
