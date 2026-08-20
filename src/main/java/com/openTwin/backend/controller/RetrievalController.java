package com.opentwin.backend.controller;

import com.opentwin.backend.model.RetrievedChunk;
import com.opentwin.backend.service.SemanticRetrievalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/retrieval")
public class RetrievalController {

    private final SemanticRetrievalService semanticRetrievalService;

    public RetrievalController(
            SemanticRetrievalService semanticRetrievalService
    ) {
        this.semanticRetrievalService =
                semanticRetrievalService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<RetrievedChunk>> search(
            @RequestParam("query") String query,
            @RequestParam(defaultValue = "5") int limit
    ) {

        String userIdString =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal()
                        .toString();

        Long userId =
                Long.parseLong(userIdString);

        List<RetrievedChunk> results =
                semanticRetrievalService.search(
                        query,
                        userId,
                        limit
                );

        return ResponseEntity.ok(results);
    }
}