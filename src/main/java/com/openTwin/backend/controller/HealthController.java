package com.opentwin.backend.controller;

import com.opentwin.backend.service.QdrantCollectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final DataSource dataSource;
    private final QdrantCollectionService qdrantCollectionService;

    public HealthController(DataSource dataSource, QdrantCollectionService qdrantCollectionService) {
        this.dataSource = dataSource;
        this.qdrantCollectionService = qdrantCollectionService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        boolean dbHealthy = false;

        try (Connection connection = dataSource.getConnection()) {
            dbHealthy = connection.isValid(2);
        } catch (Exception ignored) {
        }

        boolean qdrantHealthy = qdrantCollectionService.isAvailable();

        health.put("status", dbHealthy ? (qdrantHealthy ? "UP" : "DEGRADED") : "DOWN");
        health.put("database", dbHealthy ? "UP" : "DOWN");
        health.put("vectorSearch", qdrantHealthy ? "READY" : qdrantCollectionService.getLastStatus());
        health.put("collection", qdrantCollectionService.getCollectionName());
        health.put("message", "Open Twin backend is running");

        return ResponseEntity.ok(health);
    }
}