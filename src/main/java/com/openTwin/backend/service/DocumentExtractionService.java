package com.opentwin.backend.service;

import com.opentwin.backend.service.extraction.DocumentExtractor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentExtractionService {

    private final List<DocumentExtractor> extractors;

    public DocumentExtractionService(List<DocumentExtractor> extractors) {
        this.extractors = extractors;
    }

    public String extractText(
            MultipartFile file,
            String contentType
    ) throws IOException {

        return extractors.stream()
                .filter(extractor ->
                        extractor.supports(contentType)
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Unsupported document type"
                        )
                )
                .extractText(file);
    }
}