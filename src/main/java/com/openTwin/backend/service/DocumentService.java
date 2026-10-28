package com.opentwin.backend.service;

import com.opentwin.backend.entity.Document;
import com.opentwin.backend.entity.User;
import com.opentwin.backend.model.TextChunk;
import com.opentwin.backend.repository.DocumentRepository;
import com.opentwin.backend.repository.UserRepository;
import com.opentwin.backend.service.embedding.EmbeddingService;
import io.qdrant.client.grpc.Points.PointStruct;
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
    private final QdrantCollectionService qdrantCollectionService;

    public DocumentService(
            DocumentRepository documentRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService,
            DocumentExtractionService documentExtractionService,
            TextProcessingService textProcessingService,
            EmbeddingService embeddingService,
            QdrantVectorService qdrantVectorService,
            QdrantCollectionService qdrantCollectionService
    ) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.documentExtractionService = documentExtractionService;
        this.textProcessingService = textProcessingService;
        this.embeddingService = embeddingService;
        this.qdrantVectorService = qdrantVectorService;
        this.qdrantCollectionService = qdrantCollectionService;
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

        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file", e);
        }

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
                    documentExtractionService.extractTextFromBytes(
                            fileBytes,
                            contentType
                    );

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Failed to extract document text",
                    exception
            );
        }

        indexDocumentChunks(savedDocument.getUser().getId(), savedDocument.getId(), extractedText);

        return savedDocument;
    }

    private int indexDocumentChunks(Long userId, Long documentId, String extractedText) {
        if (!qdrantCollectionService.isAvailable()) {
            System.out.println("Qdrant is currently unavailable. Skipping vector indexing for document " + documentId);
            return 0;
        }

        List<TextChunk> chunks =
                textProcessingService.createChunks(
                        documentId,
                        extractedText
                );

        if (chunks.isEmpty()) {
            return 0;
        }

        List<String> contents = chunks.stream()
                .map(TextChunk::getContent)
                .toList();

        List<List<Float>> embeddings =
                embeddingService.generateDocumentEmbeddings(contents);

        List<PointStruct> points = new ArrayList<>(chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            TextChunk chunk = chunks.get(i);
            List<Float> embedding = embeddings.get(i);
            points.add(
                    qdrantVectorService.buildPoint(
                            userId,
                            chunk.getDocumentId(),
                            chunk.getChunkIndex(),
                            chunk.getContent(),
                            embedding
                    )
            );
        }

        qdrantVectorService.storeChunkEmbeddings(points);
        return chunks.size();
    }

    public List<Document> getUserDocuments(Long userId) {
        return documentRepository.findByUserId(userId);
    }

    public int reindexDocument(Long documentId, Long userId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + documentId));

        if (userId != null && !document.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Access denied: You do not own this document");
        }

        java.nio.file.Path filePath = java.nio.file.Paths.get(document.getStoredFilePath());
        if (!java.nio.file.Files.exists(filePath)) {
            throw new IllegalStateException("Stored file does not exist on disk: " + document.getStoredFilePath());
        }

        try {
            byte[] fileBytes = java.nio.file.Files.readAllBytes(filePath);
            String extractedText = documentExtractionService.extractTextFromBytes(fileBytes, document.getContentType());
            return indexDocumentChunks(document.getUser().getId(), document.getId(), extractedText);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file for re-indexing", e);
        }
    }

    public Map<String, Object> reindexUserDocuments(Long userId) {
        List<Document> documents = documentRepository.findByUserId(userId);
        int successCount = 0;
        int totalChunks = 0;
        List<String> errors = new ArrayList<>();

        for (Document doc : documents) {
            try {
                int chunks = reindexDocument(doc.getId(), userId);
                totalChunks += chunks;
                successCount++;
            } catch (Exception e) {
                errors.add("Document '" + doc.getOriginalFilename() + "' (ID " + doc.getId() + "): " + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalDocuments", documents.size());
        result.put("successfulDocuments", successCount);
        result.put("totalChunksIndexed", totalChunks);
        result.put("errors", errors);
        return result;
    }
}