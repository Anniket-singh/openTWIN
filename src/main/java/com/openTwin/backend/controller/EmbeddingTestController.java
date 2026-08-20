package com.opentwin.backend.controller;

import com.opentwin.backend.service.embedding.EmbeddingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/embeddings")
public class EmbeddingTestController {

    private final EmbeddingService embeddingService;

    public EmbeddingTestController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping("/test")
    public ResponseEntity<EmbeddingTestResponse> generateEmbedding(
            @RequestBody EmbeddingTestRequest request) {

        List<Float> embedding =
                embeddingService.generateDocumentEmbedding(request.text());

        List<Float> preview =
                embedding.subList(0, Math.min(5, embedding.size()));

        return ResponseEntity.ok(
                new EmbeddingTestResponse(
                        embedding.size(),
                        preview
                )
        );
    }

    public record EmbeddingTestRequest(String text) {
    }

    public record EmbeddingTestResponse(
            int dimensions,
            List<Float> preview
    ) {
    }
}