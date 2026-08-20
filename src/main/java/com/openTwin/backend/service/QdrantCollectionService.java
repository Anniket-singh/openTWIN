package com.opentwin.backend.service;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QdrantCollectionService {

    private final QdrantClient qdrantClient;
    private final String collectionName;
    private final int vectorDimension;

    public QdrantCollectionService(
            QdrantClient qdrantClient,
            @Value("${qdrant.collection-name}") String collectionName,
            @Value("${qdrant.vector-dimension}") int vectorDimension) {

        this.qdrantClient = qdrantClient;
        this.collectionName = collectionName;
        this.vectorDimension = vectorDimension;
    }

    public void initializeCollection() {

        try {
            boolean exists =
                    qdrantClient
                            .collectionExistsAsync(collectionName)
                            .get();

            if (exists) {
                return;
            }

            VectorParams vectorParams =
                    VectorParams.newBuilder()
                            .setSize(vectorDimension)
                            .setDistance(Distance.Cosine)
                            .build();

            qdrantClient
                    .createCollectionAsync(
                            collectionName,
                            vectorParams
                    )
                    .get();

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to initialize Qdrant collection",
                    exception
            );
        }
    }
}