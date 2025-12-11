package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.event.ClassGender
import no.stunor.origo.eventorapi.model.event.EventClass
import no.stunor.origo.eventorapi.model.event.EventClassTypeEnum
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
open class EventClassRepository(private val jdbcTemplate: JdbcTemplate) {

    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        EventClass(
            id = rs.getObject("id", UUID::class.java),
            eventorRef = rs.getString("eventor_ref"),
            name = rs.getString("name"),
            shortName = rs.getString("short_name"),
            type = EventClassTypeEnum.valueOf(rs.getString("type")),
            minAge = rs.getInt("min_age"),
            maxAge = rs.getInt("max_age"),
            gender = ClassGender.valueOf(rs.getString("gender")),
            presentTime = rs.getBoolean("present_time"),
            orderedResult = rs.getBoolean("ordered_result"),
            legs = rs.getInt("legs"),
            minAverageAge = rs.getInt("min_average_age"),
            maxAverageAge = rs.getInt("max_average_age")
        )
    }

    open fun findByEventId(eventId: UUID?): List<EventClass> {
        return jdbcTemplate.query(
            "SELECT * FROM class WHERE event_id = ?",
            rowMapper,
            eventId
        )
    }

    open fun findById(id: UUID): EventClass? {
        return try {
            jdbcTemplate.queryForObject(
                "SELECT * FROM class WHERE id = ?",
                rowMapper,
                id
            )
        } catch (_: Exception) {
            null
        }
    }

    open fun findByEventIdAndEventorRef(eventId: UUID, eventorRef: String): EventClass? {
        return try {
            jdbcTemplate.queryForObject(
                "SELECT * FROM class WHERE event_id = ? AND eventor_ref = ?",
                rowMapper,
                eventId, eventorRef
            )
        } catch (_: Exception) {
            null
        }
    }

    open fun save(eventClass: EventClass, eventId: UUID): EventClass {
        if (eventClass.id == null) {
            eventClass.id = UUID.randomUUID()
        }

        jdbcTemplate.update(
            """
            INSERT INTO class (id, event_id, eventor_ref, name, short_name, type, 
                min_age, max_age, gender, present_time, ordered_result, legs, 
                min_average_age, max_average_age)
            VALUES (?, ?, ?, ?, ?, ?::class_type, ?, ?, ?::class_gender, ?, ?, ?, ?, ?)
            ON CONFLICT (event_id, eventor_ref) DO UPDATE SET
                name = EXCLUDED.name,
                short_name = EXCLUDED.short_name,
                type = EXCLUDED.type,
                min_age = EXCLUDED.min_age,
                max_age = EXCLUDED.max_age,
                gender = EXCLUDED.gender,
                present_time = EXCLUDED.present_time,
                ordered_result = EXCLUDED.ordered_result,
                legs = EXCLUDED.legs,
                min_average_age = EXCLUDED.min_average_age,
                max_average_age = EXCLUDED.max_average_age
            """,
            eventClass.id, eventId, eventClass.eventorRef, eventClass.name,
            eventClass.shortName, eventClass.type.name, eventClass.minAge, eventClass.maxAge,
            eventClass.gender.name, eventClass.presentTime, eventClass.orderedResult,
            eventClass.legs, eventClass.minAverageAge, eventClass.maxAverageAge
        )

        return eventClass
    }

    open fun deleteByEventId(eventId: UUID) {
        jdbcTemplate.update("DELETE FROM class WHERE event_id = ?", eventId)
    }
}


