// File: src/main/java/au/edu/aufonduebackend/service/StorageService.java

package au.edu.aufonduebackend.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(MultipartFile file);
    void deleteFile(String fileUrl);
}