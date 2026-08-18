package com.opentwin.backend.service.extraction;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class DocxDocumentExtractor implements DocumentExtractor {
    @Override
    public boolean supports(String contentType) {
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                .equals(contentType);
    }

    @Override
    public String extractText(MultipartFile file) throws IOException {

        try (XWPFDocument document =
                     new XWPFDocument(file.getInputStream())) {

            StringBuilder text = new StringBuilder();

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                text.append(paragraph.getText());
                text.append(System.lineSeparator());
            }

            return text.toString();
        }
    }
}