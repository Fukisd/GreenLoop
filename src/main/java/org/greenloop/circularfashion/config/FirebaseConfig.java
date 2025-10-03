package org.greenloop.circularfashion.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Bean
    @ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = false)
    public FirebaseApp firebaseApp() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                ClassPathResource resource = new ClassPathResource("firebase-admin.json");
                
                if (!resource.exists()) {
                    log.warn("Firebase admin credentials file not found. Firebase features will be disabled.");
                    return null;
                }
                
                try (InputStream serviceAccount = resource.getInputStream()) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();
                    
                    FirebaseApp app = FirebaseApp.initializeApp(options);
                    log.info("Firebase initialized successfully");
                    return app;
                }
            }
            return FirebaseApp.getInstance();
        } catch (FileNotFoundException e) {
            log.warn("Firebase admin credentials file not found. Firebase features will be disabled.", e);
            return null;
        } catch (IOException e) {
            log.error("Failed to initialize Firebase", e);
            return null;
        }
    }
}
