package com.opentwin.backend.service;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.PayloadSchemaType;
import io.qdrant.client.grpc.Collections.VectorParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class QdrantCollectionService {

    private static final Logger log = LoggerFactory.getLogger(QdrantCollectionService.class);

    private final QdrantClient qdrantClient;
    private final String collectionName;
    private final int vectorDimension;
    private final AtomicBoolean available = new AtomicBoolean(false);
    private volatile String lastStatus = "Not initialized";

    public QdrantCollectionService(
            QdrantClient qdrantClient,
            @Value("${qdrant.collection-name}") String collectionName,
            @Value("${qdrant.vector-dimension}") int vectorDimension) {

        this.qdrantClient = qdrantClient;
        this.collectionName = collectionName;
        this.vectorDimension = vectorDimension;
    }

    public boolean initializeCollection() {
        try {
            log.info("Checking Qdrant collection '{}'...", collectionName);

            boolean exists = qdrantClient
                    .collectionExistsAsync(collectionName)
                    .get(10, TimeUnit.SECONDS);

            if (!exists) {
                log.info("Collection '{}' does not exist. Creating with vector size {}...", collectionName, vectorDimension);
                VectorParams vectorParams = VectorParams.newBuilder()
                        .setSize(vectorDimension)
                        .setDistance(Distance.Cosine)
                        .build();

                qdrantClient
                        .createCollectionAsync(collectionName, vectorParams)
                        .get(15, TimeUnit.SECONDS);
                log.info("Collection '{}' created successfully.", collectionName);
            } else {
                log.info("Collection '{}' already exists and is ready.", collectionName);
            }

            // Ensure payload index on userId exists for strict mode filtering
            try {
                qdrantClient.createPayloadIndexAsync(
                        collectionName,
                        "userId",
                        PayloadSchemaType.Integer,
                        null,
                        true,
                        null,
                        Duration.ofSeconds(10)
                ).get();
                log.info("Payload index for 'userId' ensured on collection '{}'.", collectionName);
            } catch (Exception indexException) {
                log.info("Payload index note for 'userId': {}", indexException.getMessage());
            }

            available.set(true);
            lastStatus = "READY";
            return true;

        } catch (Exception exception) {
            available.set(false);
            lastStatus = "UNAVAILABLE: " + exception.getMessage();
            log.warn("Could not connect to Qdrant or initialize collection '{}': {}. "
                    + "The backend will continue running, but vector search/retrieval features will be degraded "
                    + "until a reachable Qdrant cluster is configured.", collectionName, exception.getMessage());
            return false;
        }
    }

    public boolean isAvailable() {
        return available.get();
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public int getVectorDimension() {
        return vectorDimension;
    }
}