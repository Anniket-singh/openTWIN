package com.opentwin.backend.controller;

import com.opentwin.backend.entity.Document;
import com.opentwin.backend.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

import java.util.Set;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity
                    .status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("File size cannot exceed 10 MB");
        }

        String contentType = resolveContentType(file);

        System.out.println("Uploaded filename: " + file.getOriginalFilename());
        System.out.println("Uploaded content type: " + contentType);

        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES.contains(contentType)) {

            return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body("Unsupported file type. Only PDF, DOCX, and TXT are allowed.");
        }

        String userIdString = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        Long userId = Long.parseLong(userIdString);

        Document document = documentService.uploadDocument(
                file,
                userId,
                contentType
        );

        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping
    public ResponseEntity<List<Document>> getUserDocuments() {

        String userIdString = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        Long userId = Long.parseLong(userIdString);

        return ResponseEntity.ok(
                documentService.getUserDocuments(userId)
        );
    }

    @PostMapping("/reindex")
    public ResponseEntity<java.util.Map<String, Object>> reindexAll() {
        String userIdString = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        Long userId = Long.parseLong(userIdString);
        java.util.Map<String, Object> result = documentService.reindexUserDocuments(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/reindex")
    public ResponseEntity<java.util.Map<String, Object>> reindexOne(@PathVariable("id") Long documentId) {
        String userIdString = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        Long userId = Long.parseLong(userIdString);
        int chunks = documentService.reindexDocument(documentId, userId);
        return ResponseEntity.ok(java.util.Map.of(
                "documentId", documentId,
                "chunksIndexed", chunks,
                "message", "Document reindexed successfully"
        ));
    }

    private String resolveContentType(MultipartFile file) {

        String contentType = file.getContentType();

        if (contentType != null &&
                !contentType.equals("application/octet-stream")) {
            return contentType;
        }

        String filename = file.getOriginalFilename();

        if (filename == null) {
            return null;
        }

        String lowerCaseFilename =
                filename.toLowerCase(Locale.ROOT);

        if (lowerCaseFilename.endsWith(".pdf")) {
            return "application/pdf";
        }

        if (lowerCaseFilename.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }

        if (lowerCaseFilename.endsWith(".txt")) {
            return "text/plain";
        }

        return null;
    }
}