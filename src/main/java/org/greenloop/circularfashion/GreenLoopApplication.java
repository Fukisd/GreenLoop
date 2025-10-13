package org.greenloop.circularfashion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class GreenLoopApplication {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(GreenLoopApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("🚀 Green Loop Backend is ready!");
        System.out.println("🌐 Server running on port: " + environment.getProperty("server.port"));
        System.out.println("📊 Actuator endpoints available at: /actuator");
        System.out.println("❤️ Health check available at: /api/public/health");
        System.out.println("⏰ Application startup completed at: " + java.time.LocalDateTime.now());
    }

}
