package no.stunor.origo.eventorapi.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException
import java.sql.Timestamp
import java.time.format.DateTimeFormatter

@Configuration
open class JacksonConfig {

    @Bean
    open fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        val javaTimeModule = JavaTimeModule()

        // Register custom serializer for java.sql.Timestamp
        javaTimeModule.addSerializer(Timestamp::class.java, TimestampSerializer())

        mapper.registerModule(javaTimeModule)
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        return mapper
    }

    class TimestampSerializer : StdSerializer<Timestamp>(Timestamp::class.java) {
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

        @Throws(IOException::class)
        override fun serialize(value: Timestamp, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeString(value.toInstant().atZone(java.time.ZoneOffset.UTC).format(formatter))
        }
    }
}