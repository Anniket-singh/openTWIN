package com.opentwin.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDirectory;

    public FileStorageService() {
        this.uploadDirectory = Paths.get("uploads")
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException exception) {
            throw new RuntimeException("Could not create upload directory", exception);
        }
    }

    public String storeFile(MultipartFile file, Long userId) {

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Invalid filename");
        }

        String extension = "";

        int lastDotIndex = originalFilename.lastIndexOf('.');

        if (lastDotIndex >= 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        String storedFilename =
                UUID.randomUUID() + extension;

        Path userDirectory = uploadDirectory
                .resolve(String.valueOf(userId))
                .normalize();

        try {
            Files.createDirectories(userDirectory);

            Path targetPath = userDirectory
                    .resolve(storedFilename)
                    .normalize();

            file.transferTo(targetPath);

            return targetPath.toString();

        } catch (IOException exception) {
            throw new RuntimeException("Could not store file", exception);
        }
    }
}