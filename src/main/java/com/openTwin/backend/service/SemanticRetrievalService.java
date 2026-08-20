package com.opentwin.backend.service;

import com.opentwin.backend.model.RetrievedChunk;
import com.opentwin.backend.service.embedding.EmbeddingService;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Common.Filter;
import io.qdrant.client.grpc.Points;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static io.qdrant.client.ConditionFactory.match;

@Service
public class SemanticRetrievalService {

    private final QdrantClient qdrantClient;
    private final EmbeddingService embeddingService;
    private final String collectionName;

    public SemanticRetrievalService(
            QdrantClient qdrantClient,
            EmbeddingService embeddingService,
            @Value("${qdrant.collection-name}") String collectionName
    ) {
        this.qdrantClient = qdrantClient;
        this.embeddingService = embeddingService;
        this.collectionName = collectionName;
    }

    public List<RetrievedChunk> search(
            String query,
            Long userId,
            int limit
    ) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID cannot be null"
            );
        }

        if (limit <= 0) {
            throw new IllegalArgumentException(
                    "Limit must be greater than zero"
            );
        }

        List<Float> queryVector =
                embeddingService.generateQueryEmbedding(query);

        try {

            Filter userFilter =
                    Filter.newBuilder()
                            .addMust(
                                    match("userId", userId)
                            )
                            .build();

            List<Points.ScoredPoint> results =
                    qdrantClient
                            .searchAsync(
                                    Points.SearchPoints.newBuilder()
                                            .setCollectionName(collectionName)
                                            .addAllVector(queryVector)

                                            // 🔐 USER ISOLATION
                                            .setFilter(userFilter)

                                            .setLimit(limit)

                                            .setWithPayload(
                                                    Points.WithPayloadSelector
                                                            .newBuilder()
                                                            .setEnable(true)
                                                            .build()
                                            )

                                            .build()
                            )
                            .get();

            List<RetrievedChunk> chunks =
                    new ArrayList<>();

            for (Points.ScoredPoint point : results) {

                Long documentId =
                        point.getPayloadOrThrow("documentId")
                                .getIntegerValue();

                int chunkIndex =
                        (int) point
                                .getPayloadOrThrow("chunkIndex")
                                .getIntegerValue();

                String content =
                        point.getPayloadOrThrow("content")
                                .getStringValue();

                chunks.add(
                        new RetrievedChunk(
                                documentId,
                                chunkIndex,
                                content,
                                point.getScore()
                        )
                );
            }

            return chunks;

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to search Qdrant",
                    exception
            );
        }
    }
}