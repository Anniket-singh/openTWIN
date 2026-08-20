package com.opentwin.backend.service.embedding;

import com.google.genai.Client;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import com.google.genai.types.ContentEmbedding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {

    private final Client geminiClient;
    private final String embeddingModel;
    private final int embeddingDimension;

    public EmbeddingService(
            Client geminiClient,
            @Value("${gemini.embedding.model}") String embeddingModel,
            @Value("${gemini.embedding.dimension}") int embeddingDimension) {

        this.geminiClient = geminiClient;
        this.embeddingModel = embeddingModel;
        this.embeddingDimension = embeddingDimension;
    }

    public List<Float> generateDocumentEmbedding(String text) {

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }

        EmbedContentConfig config = EmbedContentConfig.builder()
                .taskType("RETRIEVAL_DOCUMENT")
                .outputDimensionality(embeddingDimension)
                .build();

        EmbedContentResponse response =
                geminiClient.models.embedContent(
                        embeddingModel,
                        text,
                        config
                );

        List<ContentEmbedding> embeddings =
                response.embeddings()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Gemini returned no embeddings"
                                ));

        if (embeddings.isEmpty()) {
            throw new IllegalStateException(
                    "Gemini returned an empty embedding list"
            );
        }

        List<Float> vector =
                embeddings.get(0)
                        .values()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Gemini returned an embedding without values"
                                ));

        if (vector.size() != embeddingDimension) {
            throw new IllegalStateException(
                    "Unexpected embedding dimension. Expected "
                            + embeddingDimension
                            + " but received "
                            + vector.size()
            );
        }

        return vector;
    }

    public List<Float> generateQueryEmbedding(String query) {

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Query cannot be empty");
        }

        EmbedContentConfig config = EmbedContentConfig.builder()
                .taskType("RETRIEVAL_QUERY")
                .outputDimensionality(embeddingDimension)
                .build();

        EmbedContentResponse response =
                geminiClient.models.embedContent(
                        embeddingModel,
                        query,
                        config
                );

        List<ContentEmbedding> embeddings =
                response.embeddings()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Gemini returned no query embedding"
                                ));

        if (embeddings.isEmpty()) {
            throw new IllegalStateException(
                    "Gemini returned an empty query embedding"
            );
        }

        List<Float> vector =
                embeddings.get(0)
                        .values()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Gemini returned a query embedding without values"
                                ));

        if (vector.size() != embeddingDimension) {
            throw new IllegalStateException(
                    "Unexpected query embedding dimension. Expected "
                            + embeddingDimension
                            + " but received "
                            + vector.size()
            );
        }

        return vector;
    }
}