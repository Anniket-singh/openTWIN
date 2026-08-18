package com.opentwin.backend.service;

import com.opentwin.backend.entity.Document;
import com.opentwin.backend.entity.User;
import com.opentwin.backend.repository.DocumentRepository;
import com.opentwin.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    private final DocumentExtractionService documentExtractionService;

    public DocumentService(
            DocumentRepository documentRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService,

            DocumentExtractionService documentExtractionService
    ) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;

        this.documentExtractionService = documentExtractionService;
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

        // temp test

        String extractedText =
                null;
        try {
            extractedText =
                    documentExtractionService.extractText(
                            file,
                            contentType
                    );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("===== EXTRACTED TEXT =====");
        System.out.println(extractedText);
        System.out.println("==========================");

        return documentRepository.save(document);
    }
}