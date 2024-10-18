package no.stunor.origo.eventorapi

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.FileInputStream
import java.io.FileNotFoundException

@SpringBootApplication
open class Application

fun main(args: Array<String>) {
    initializeFirebase()
    runApplication<Application>(*args)
}

fun initializeFirebase() {
    try {
        val serviceAccount = FileInputStream("serviceAccountKey.json")
        val credentials = GoogleCredentials.fromStream(serviceAccount)
        val options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build()
        FirebaseApp.initializeApp(options)
    } catch (e: FileNotFoundException) {
        throw RuntimeException("serviceAccountKey.json file not found", e)
    }
}