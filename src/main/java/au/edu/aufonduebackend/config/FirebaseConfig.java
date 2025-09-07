package au.edu.aufonduebackend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                GoogleCredentials credentials;

                // Try to load from environment variable first (for production)
                String firebaseCredentials = System.getenv("FIREBASE_CREDENTIALS");
                if (firebaseCredentials != null && !firebaseCredentials.isEmpty()) {
                    logger.info("Loading Firebase credentials from environment variable");
                    InputStream credentialsStream = new ByteArrayInputStream(firebaseCredentials.getBytes());
                    credentials = GoogleCredentials.fromStream(credentialsStream);
                } else {
                    // Fall back to local file (for development)
                    logger.info("Loading Firebase credentials from local file");
                    credentials = GoogleCredentials.fromStream(
                            new ClassPathResource("firebase-service-account.json").getInputStream());
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .build();

                FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully");
            }
        } catch (IOException e) {
            logger.error("Failed to initialize Firebase - Firebase features will be disabled", e);
            logger.warn("Firebase is not initialized. Password reset and other Firebase features will not work.");
            // Don't throw exception - allow the app to start without Firebase
        } catch (Exception e) {
            logger.error("Unexpected error initializing Firebase - Firebase features will be disabled", e);
            logger.warn("Firebase is not initialized. Password reset and other Firebase features will not work.");
            // Don't throw exception - allow the app to start without Firebase
        }
    }
}