package com.opentwin.backend.controller;

import com.opentwin.backend.service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(
            @RequestParam("prompt") String prompt
    ) {

        String response =
                geminiService.generateResponse(prompt);

        return ResponseEntity.ok(response);
    }
}