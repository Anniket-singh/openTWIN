package com.opentwin.backend.service;

import com.opentwin.backend.entity.Document;
import com.opentwin.backend.entity.User;
import com.opentwin.backend.model.TextChunk;
import com.opentwin.backend.repository.DocumentRepository;
import com.opentwin.backend.repository.UserRepository;
import com.opentwin.backend.service.embedding.EmbeddingService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

import java.io.IOException;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    private final DocumentExtractionService documentExtractionService;

    private final TextProcessingService textProcessingService;

    private final EmbeddingService embeddingService;
    private final QdrantVectorService qdrantVectorService;

    public DocumentService(
            DocumentRepository documentRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService,
            DocumentExtractionService documentExtractionService,
            TextProcessingService textProcessingService,
            EmbeddingService embeddingService,
            QdrantVectorService qdrantVectorService
    ) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.documentExtractionService = documentExtractionService;
        this.textProcessingService = textProcessingService;
        this.embeddingService = embeddingService;
        this.qdrantVectorService = qdrantVectorService;
    }

    public Document uploadDocument(
            MultipartFile file,
            Long userId,
            String contentType
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        String storedFilePath =
                fileStorageService.storeFile(file, userId);

        Document document = new Document();

        document.setOriginalFilename(
                file.getOriginalFilename()
        );

        document.setContentType(contentType);

        document.setFileSize(
                file.getSize()
        );

        document.setStoredFilePath(
                storedFilePath
        );

        document.setUser(user);

        /*
         * Saving first so PostgreSQL generates the document ID.
         */
        Document savedDocument =
                documentRepository.save(document);

        String extractedText;

        try {

            extractedText =
                    documentExtractionService.extractText(
                            file,
                            contentType
                    );

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Failed to extract document text",
                    exception
            );
        }

        List<TextChunk> chunks =
                textProcessingService.createChunks(
                        savedDocument.getId(),
                        extractedText
                );

        System.out.println("===== CHUNKS =====");

        for (TextChunk chunk : chunks) {

            System.out.println(
                    "Chunk " +
                            chunk.getChunkIndex() +
                            ": " +
                            chunk.getContent()
            );

            List<Float> embedding =
                    embeddingService.generateDocumentEmbedding(
                            chunk.getContent()
                    );

            qdrantVectorService.storeChunkEmbedding(
                    savedDocument.getUser().getId(),
                    chunk.getDocumentId(),
                    chunk.getChunkIndex(),
                    chunk.getContent(),
                    embedding
            );
        }

        System.out.println("==================");

        return savedDocument;
    }
}