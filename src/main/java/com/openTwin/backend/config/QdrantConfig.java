package com.opentwin.backend.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QdrantConfig {

    private static final Logger log = LoggerFactory.getLogger(QdrantConfig.class);

    @Bean
    public QdrantClient qdrantClient(
            @Value("${qdrant.host:localhost}") String host,
            @Value("${qdrant.grpc-port:6334}") int grpcPort,
            @Value("${qdrant.api-key:}") String apiKey,
            @Value("${qdrant.use-tls:false}") boolean useTls) {

        String cleanHost = host != null ? host.trim() : "localhost";

        // Clean protocol prefix if user provided full URL
        if (cleanHost.startsWith("https://")) {
            cleanHost = cleanHost.substring("https://".length());
            useTls = true;
        } else if (cleanHost.startsWith("http://")) {
            cleanHost = cleanHost.substring("http://".length());
        }

        // Clean trailing slashes or ports if present in host string
        if (cleanHost.contains("/")) {
            cleanHost = cleanHost.substring(0, cleanHost.indexOf('/'));
        }
        if (cleanHost.contains(":")) {
            String[] parts = cleanHost.split(":");
            cleanHost = parts[0];
            try {
                grpcPort = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {
            }
        }

        // Auto-detect TLS for Qdrant Cloud hosts
        boolean effectiveTls = useTls || cleanHost.toLowerCase().contains("qdrant.io")
                || cleanHost.toLowerCase().contains("qdrant.tech");

        log.info("Configuring QdrantClient: host={}, port={}, useTls={}", cleanHost, grpcPort, effectiveTls);

        QdrantGrpcClient.Builder builder = QdrantGrpcClient.newBuilder(cleanHost, grpcPort, effectiveTls);

        if (apiKey != null && !apiKey.trim().isEmpty()) {
            builder.withApiKey(apiKey.trim());
        }

        return new QdrantClient(builder.build());
    }
}