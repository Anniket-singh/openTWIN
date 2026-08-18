package com.opentwin.backend.service;

import com.opentwin.backend.model.TextChunk;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextProcessingService {

    private static final int CHUNK_SIZE = 1000;
    private static final int CHUNK_OVERLAP = 200;

    public String cleanText(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replaceAll("[ \t]+", " ")
                .replaceAll("\n{3,}", "\n\n")
                .trim();
    }

    public List<TextChunk> createChunks(Long documentId, String text) {

        String cleanedText = cleanText(text);

        if (cleanedText.isBlank()) {
            return List.of();
        }

        final int CHUNK_SIZE = 1000;
        final int CHUNK_OVERLAP = 200;

        List<TextChunk> chunks = new ArrayList<>();

        int start = 0;
        int chunkIndex = 0;

        while (start < cleanedText.length()) {

            int end = Math.min(
                    start + CHUNK_SIZE,
                    cleanedText.length()
            );

            // Don't cut a word in half.
            if (end < cleanedText.length()) {

                int lastSpace = cleanedText.lastIndexOf(' ', end);

                if (lastSpace > start) {
                    end = lastSpace;
                }
            }

            String chunkContent =
                    cleanedText.substring(start, end).trim();

            if (!chunkContent.isBlank()) {

                chunks.add(
                        new TextChunk(
                                documentId,
                                chunkIndex,
                                chunkContent
                        )
                );

                chunkIndex++;
            }

            if (end >= cleanedText.length()) {
                break;
            }

            int nextStart = end - CHUNK_OVERLAP;

            // Make sure we don't move backwards.
            start = Math.max(nextStart, start + 1);
        }

        return chunks;
    }
}