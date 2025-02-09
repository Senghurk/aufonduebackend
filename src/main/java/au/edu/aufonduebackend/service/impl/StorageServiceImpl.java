// File: src/main/java/au/edu/aufonduebackend/service/impl/StorageServiceImpl.java

package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {
    private final Path root = Paths.get("uploads");

    public StorageServiceImpl() {
        try {
            Files.createDirectories(root);
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Invalid file type. Only images are allowed.");
            }

            String filename = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.root.resolve(filename));
            return "/uploads/" + filename;
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Files.deleteIfExists(this.root.resolve(filename));
        } catch (Exception e) {
            throw new RuntimeException("Could not delete the file", e);
        }
    }
}