package com.ceoms.storage;

import com.ceoms.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${ceoms.upload.dir}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String storeFile(MultipartFile file, String subDir) {
        String cleanSubDir = StringUtils.cleanPath(subDir != null ? subDir : "");
        Path targetDir = uploadDir.resolve(cleanSubDir).normalize();
        if (!targetDir.startsWith(this.uploadDir)) {
            throw new BadRequestException("Security Violation: Cannot store file outside upload directory");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) extension = originalName.substring(dotIndex).toLowerCase();
        String fileName = UUID.randomUUID() + extension;

        try {
            Files.createDirectories(targetDir);
            Path targetPath = targetDir.resolve(fileName).normalize();
            if (!targetPath.startsWith(this.uploadDir)) {
                throw new BadRequestException("Security Violation: Target path is invalid");
            }
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return (cleanSubDir.isBlank() ? "" : cleanSubDir + "/") + fileName;
        } catch (IOException e) {
            throw new BadRequestException("Failed to store file: " + originalName);
        }
    }

    public Resource loadFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new BadRequestException("File path is required");
        }
        try {
            Path path = uploadDir.resolve(filePath).normalize();
            if (!path.startsWith(this.uploadDir)) {
                throw new BadRequestException("Security Violation: Path traversal attempt detected");
            }
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) return resource;
            throw new BadRequestException("File not found or not readable: " + filePath);
        } catch (MalformedURLException e) {
            throw new BadRequestException("File not found: " + filePath);
        }
    }
}
