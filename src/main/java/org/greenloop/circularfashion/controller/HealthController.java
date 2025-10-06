package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    private volatile boolean isReady = false;

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the API is running")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> response = new HashMap<>();
        
        if (isReady) {
            response.put("status", "UP");
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "Green Loop API is running successfully!");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "STARTING");
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "Application is still starting up...");
            return ResponseEntity.status(503).body(response);
        }
    }

    @GetMapping("/actuator/health")
    @Operation(summary = "Actuator health check", description = "Railway health check endpoint")
    public ResponseEntity<Map<String, Object>> getActuatorHealth() {
        return getHealth();
    }

    @Override
    public Health health() {
        try {
            // Check database connection
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(5)) {
                    isReady = true;
                    return Health.up()
                            .withDetail("database", "Connected")
                            .withDetail("timestamp", LocalDateTime.now())
                            .build();
                }
            }
        } catch (Exception e) {
            isReady = false;
            return Health.down()
                    .withDetail("database", "Connection failed: " + e.getMessage())
                    .withDetail("timestamp", LocalDateTime.now())
                    .build();
        }
        
        isReady = false;
        return Health.down()
                .withDetail("database", "Not connected")
                .withDetail("timestamp", LocalDateTime.now())
                .build();
    }
} 