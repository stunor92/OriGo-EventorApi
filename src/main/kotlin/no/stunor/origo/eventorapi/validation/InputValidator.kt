package no.stunor.origo.eventorapi.validation

import org.springframework.stereotype.Component

/**
 * Validator for user input to prevent SSRF and injection attacks
 */
@Component
class InputValidator {

    companion object {
        // 3-letter uppercase country code (e.g., NOR, SWE, FIN)
        private val EVENTOR_ID_PATTERN = Regex("^[A-Z]{3}$")

        // Numeric event IDs (typical for Eventor API)
        private val EVENT_ID_PATTERN = Regex("^[0-9]{1,20}$")

        // Alphanumeric person IDs with optional hyphens
        private val PERSON_ID_PATTERN = Regex("^[0-9-]{1,20}$")
    }

    /**
     * Validates eventor ID to prevent SSRF attacks
     * @throws IllegalArgumentException if invalid
     */
    fun validateEventorId(eventorId: String): String {
        require(EVENTOR_ID_PATTERN.matches(eventorId)) {
            "Invalid eventor ID format. Must be a 3-letter uppercase country code (e.g., NOR, SWE, FIN)."
        }
        return eventorId
    }

    /**
     * Validates event ID to prevent SSRF attacks
     * @throws IllegalArgumentException if invalid
     */
    fun validateEventId(eventId: String): String {
        require(EVENT_ID_PATTERN.matches(eventId)) {
            "Invalid event ID format. Must be numeric."
        }
        return eventId
    }

    /**
     * Validates person ID to prevent SSRF attacks
     * @throws IllegalArgumentException if invalid
     */
    fun validatePersonId(personId: String): String {
        require(PERSON_ID_PATTERN.matches(personId)) {
            "Invalid event ID format. Must be numeric."
        }
        return personId
    }

    /**
     * Validates username to prevent injection attacks
     * @throws IllegalArgumentException if invalid
     */
    fun validateUsername(username: String): String {
        require(username.length in 1..100) {
            "Username must be between 1 and 100 characters."
        }
        // Prevent null bytes and control characters
        require(!username.contains('\u0000') && username.all { it.code >= 32 }) {
            "Username contains invalid characters."
        }
        return username
    }
}

