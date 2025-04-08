package no.stunor.origo.eventorapi.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset

class TimestampISO8601Serializer : JsonSerializer<Timestamp>() {
    override fun serialize(value: Timestamp, gen: JsonGenerator, serializers: SerializerProvider) {
        val iso8601String = value.toInstant().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        gen.writeString(iso8601String)
    }
}

class TimestampListISO8601Serializer : JsonSerializer<List<Timestamp>>() {
    override fun serialize(value: List<Timestamp>, gen: JsonGenerator, serializers: SerializerProvider) {
        val iso8601List = value.map { it.toInstant().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) }
        gen.writeStartArray()
        iso8601List.forEach { gen.writeString(it) }
        gen.writeEndArray()
    }
}