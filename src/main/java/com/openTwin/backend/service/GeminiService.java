package com.opentwin.backend.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client geminiClient;
    private final String generationModel;

    public GeminiService(
            Client geminiClient,
            @Value("${gemini.generation.model}") String generationModel
    ) {
        this.geminiClient = geminiClient;
        this.generationModel = generationModel;
    }

    public String generateResponse(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "Prompt cannot be empty"
            );
        }

        try {

            GenerateContentResponse response =
                    geminiClient.models.generateContent(
                            generationModel,
                            prompt,
                            null
                    );

            String responseText = response.text();

            if (responseText == null || responseText.isBlank()) {
                throw new IllegalStateException(
                        "Gemini returned an empty response"
                );
            }

            return responseText;

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to generate response from Gemini",
                    exception
            );
        }
    }
}