package com.opentwin.backend.config;

import com.opentwin.backend.service.QdrantCollectionService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QdrantInitializer {

    @Bean
    public ApplicationRunner initializeQdrant(
            QdrantCollectionService qdrantCollectionService) {

        return args -> {
            qdrantCollectionService.initializeCollection();
        };
    }
}