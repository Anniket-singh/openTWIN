package com.opentwin.backend.controller;

import com.opentwin.backend.model.RagResponse;
import com.opentwin.backend.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/ask")
    public ResponseEntity<RagResponse> ask(
            @RequestParam("question") String question,
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

        RagResponse response =
                ragService.answerQuestion(
                        question,
                        userId,
                        limit
                );

        return ResponseEntity.ok(response);
    }
}