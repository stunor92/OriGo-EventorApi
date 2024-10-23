package no.stunor.origo.eventorapi.data

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.QuerySnapshot
import com.google.firebase.cloud.FirestoreClient
import no.stunor.origo.eventorapi.model.person.Person
import org.springframework.stereotype.Repository

@Repository
class PersonRepository {
    private val firestore = FirestoreClient.getFirestore()

    fun findByPersonIdAndEventorId(personId: String, eventorId: String): Person? {
        val future: ApiFuture<QuerySnapshot> = firestore.collection("persons")
            .whereEqualTo("personId", personId)
            .whereEqualTo("eventorId", eventorId)
            .get()

        return if(future.get().isEmpty){
            null
        } else {
            future.get().documents.first().toObject(Person::class.java)
        }
    }

    fun findAllByUserId(userId: String): List<Person> {
        val future: ApiFuture<QuerySnapshot> = firestore.collection("persons")
            .whereArrayContains("users", userId)
            .get()

        val documents = future.get().documents

        val result: MutableList<Person> = mutableListOf()

        for (document in documents) {
            result.add(document.toObject(Person::class.java))
        }
        return result.toList()
    }

    fun findAllByUserIdAndEventorId(userId: String, eventorId: String): List<Person> {
        val future: ApiFuture<QuerySnapshot> = firestore.collection("persons")
            .whereArrayContains("users", userId)
            .whereEqualTo("eventorId", eventorId)
            .get()

        val documents = future.get().documents

        val result: MutableList<Person> = mutableListOf()

        for (document in documents) {
            result.add(document.toObject(Person::class.java))
        }
        return result.toList()
    }

    fun save(person: Person) {
        if(person.id == null) {
            firestore.collection("persons").add(person)
        } else {
            firestore.collection("persons").document(person.id!!).set(person)
        }
    }
}