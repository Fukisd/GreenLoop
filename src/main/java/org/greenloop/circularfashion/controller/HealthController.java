package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the API is running")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Green Loop API is running successfully!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/actuator/health")
    @Operation(summary = "Actuator health check", description = "Railway health check endpoint")
    public ResponseEntity<Map<String, Object>> getActuatorHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Green Loop API is healthy!");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/")
    @Operation(summary = "Root endpoint", description = "Simple root endpoint")
    public ResponseEntity<String> getRoot() {
        return ResponseEntity.ok("Green Loop API is running!");
    }
} 