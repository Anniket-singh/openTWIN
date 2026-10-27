package com.opentwin.backend.config;

import com.opentwin.backend.service.QdrantCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QdrantInitializer {

    private static final Logger log = LoggerFactory.getLogger(QdrantInitializer.class);

    @Bean
    public ApplicationRunner initializeQdrant(
            QdrantCollectionService qdrantCollectionService) {

        return args -> {
            boolean success = qdrantCollectionService.initializeCollection();
            if (success) {
                log.info("Qdrant vector collection initialized and ready.");
            } else {
                log.warn("Qdrant vector collection could not be initialized at startup. "
                        + "Authentication and relational data are fully operational. "
                        + "Vector retrieval will become active once Qdrant cluster is reachable.");
            }
        };
    }
}