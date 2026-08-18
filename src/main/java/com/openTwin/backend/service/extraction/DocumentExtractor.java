package com.opentwin.backend.service.extraction;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DocumentExtractor {

    boolean supports(String contentType);

    String extractText(MultipartFile file) throws IOException;
}