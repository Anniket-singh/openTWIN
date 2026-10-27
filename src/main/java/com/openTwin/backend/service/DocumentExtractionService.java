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

    public String extractTextFromBytes(
            byte[] bytes,
            String contentType
    ) throws IOException {
        if ("application/pdf".equals(contentType)) {
            try (org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.Loader.loadPDF(bytes)) {
                return new org.apache.pdfbox.text.PDFTextStripper().getText(document);
            }
        }
        if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) {
            try (org.apache.poi.xwpf.usermodel.XWPFDocument document =
                         new org.apache.poi.xwpf.usermodel.XWPFDocument(new java.io.ByteArrayInputStream(bytes))) {
                StringBuilder text = new StringBuilder();
                for (org.apache.poi.xwpf.usermodel.XWPFParagraph paragraph : document.getParagraphs()) {
                    text.append(paragraph.getText());
                    text.append(System.lineSeparator());
                }
                return text.toString();
            }
        }
        if ("text/plain".equals(contentType)) {
            return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        }
        throw new IllegalArgumentException("Unsupported document type: " + contentType);
    }
}