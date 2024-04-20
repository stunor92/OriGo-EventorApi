package no.stunor.origo.eventorapi.model.person

import com.google.cloud.firestore.annotation.DocumentId
import com.google.cloud.spring.data.firestore.Document
import java.io.Serializable

@Document(collectionName = "persons")
data class Person(
        @DocumentId
        var id: String? = null,
        var eventorId: String = "",
        var personId: String = "",
        var name: PersonName = PersonName(),
        var birthYear: Int = 0,
        var nationality: String = "",
        var gender: Gender = Gender.OTHER,
        var users: MutableList<String> = mutableListOf(),
        var mobilePhone: String? = null,
        var email: String? = null,
        var memberships: Map<String, MembershipType> = HashMap(),
) : Serializable
