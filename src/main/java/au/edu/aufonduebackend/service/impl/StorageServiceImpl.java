// File: src/main/java/au/edu/aufonduebackend/service/impl/StorageServiceImpl.java

package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.service.StorageService;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final BlobContainerClient blobContainerClient;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            System.out.println("Starting file upload to Azure Blob Storage");
            System.out.println("Container Client exists: " + (blobContainerClient != null));
            System.out.println("Container Name: " + blobContainerClient.getBlobContainerName());

            // Generate a unique filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = String.format("%s-%s%s", timestamp, UUID.randomUUID(), extension);

            System.out.println("Generated filename: " + filename);

            // Get a reference to a blob
            BlobClient blobClient = blobContainerClient.getBlobClient(filename);
            System.out.println("Created blob client for: " + blobClient.getBlobUrl());

            // Upload the file
            System.out.println("File size: " + file.getSize());
            blobClient.upload(new ByteArrayInputStream(file.getBytes()), file.getSize(), true);

            System.out.println("File uploaded successfully");
            String blobUrl = blobClient.getBlobUrl();
            System.out.println("Blob URL: " + blobUrl);

            return blobUrl;
        } catch (Exception e) {
            System.err.println("Error uploading to Azure Blob Storage: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file to Azure Blob Storage", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            // Extract the blob name from the URL
            String blobName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            // Get a reference to the blob and delete it
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            blobClient.deleteIfExists();

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from Azure Blob Storage", e);
        }
    }
}