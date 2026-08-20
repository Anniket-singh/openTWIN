package com.opentwin.backend.service;

import io.qdrant.client.PointIdFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.ValueFactory;
import io.qdrant.client.VectorsFactory;
import io.qdrant.client.grpc.Points.PointStruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class QdrantVectorService {

    private final QdrantClient qdrantClient;
    private final String collectionName;

    public QdrantVectorService(
            QdrantClient qdrantClient,
            @Value("${qdrant.collection-name}") String collectionName) {

        this.qdrantClient = qdrantClient;
        this.collectionName = collectionName;
    }

    public void storeChunkEmbedding(
            Long userId,
            Long documentId,
            int chunkIndex,
            String content,
            List<Float> embedding) {

        if (embedding == null || embedding.isEmpty()) {
            throw new IllegalArgumentException(
                    "Embedding cannot be empty"
            );
        }

        UUID pointUuid = UUID.nameUUIDFromBytes(
                (documentId + ":" + chunkIndex)
                        .getBytes(StandardCharsets.UTF_8)
        );

        PointStruct point =
                PointStruct.newBuilder()
                        .setId(PointIdFactory.id(pointUuid))
                        .setVectors(VectorsFactory.vectors(embedding))
                        .putAllPayload(
                                Map.of(
                                        "userId",
                                        ValueFactory.value(userId),
                                        "documentId",
                                        ValueFactory.value(documentId),
                                        "chunkIndex",
                                        ValueFactory.value(chunkIndex),
                                        "content",
                                        ValueFactory.value(content)
                                )
                        )
                        .build();

        try {
            qdrantClient
                    .upsertAsync(
                            collectionName,
                            List.of(point)
                    )
                    .get();

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to store embedding in Qdrant",
                    exception
            );
        }
    }
}