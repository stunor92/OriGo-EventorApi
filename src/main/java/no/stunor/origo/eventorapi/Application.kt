package no.stunor.origo.eventorapi;

import com.google.firebase.FirebaseApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		FirebaseApp.initializeApp();
		SpringApplication.run(Application.class, args);
	}
}
