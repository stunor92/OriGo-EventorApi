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
    var credentials: GoogleCredentials
    try {
        val serviceAccount =
            FileInputStream("serviceAccountKey.json")
        credentials = GoogleCredentials.fromStream(serviceAccount)
    } catch (e: FileNotFoundException) {
        credentials =  GoogleCredentials.getApplicationDefault()
    }
    val options = FirebaseOptions.builder()
        .setCredentials(credentials)
        .build()

    FirebaseApp.initializeApp(options)
}