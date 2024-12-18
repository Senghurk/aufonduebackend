package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.model.dto.request.IssueRequest;
import au.edu.aufonduebackend.model.dto.response.IssueResponse;
import au.edu.aufonduebackend.model.dto.response.UserResponse;
import au.edu.aufonduebackend.model.entity.Issue;
import au.edu.aufonduebackend.repository.IssueRepository;
import au.edu.aufonduebackend.service.IssueService;
import au.edu.aufonduebackend.service.StorageService;
import au.edu.aufonduebackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final StorageService storageService;

    @Override
    @Transactional
    public IssueResponse createIssue(IssueRequest request, List<MultipartFile> photos) {
        // Input validation
        validateIssueRequest(request);

        // Create new issue
        Issue issue = new Issue();
        issue.setDescription(request.getDescription());

        // Handle location based on usingCustomLocation
        if (request.isUsingCustomLocation()) {
            issue.setCustomLocation(request.getCustomLocation());
            issue.setLatitude(null);
            issue.setLongitude(null);
        } else {
            issue.setLatitude(request.getLatitude());
            issue.setLongitude(request.getLongitude());
            issue.setCustomLocation(null);
        }
        issue.setUsingCustomLocation(request.isUsingCustomLocation());

        // Handle category
        if ("Custom".equals(request.getCategory())) {
            issue.setCategory(request.getCustomCategory());
        } else {
            issue.setCategory(request.getCategory());
        }

        issue.setStatus("PENDING"); // Default status
        issue.setPhotoUrls(new ArrayList<>()); // Initialize empty list

        // Handle photo uploads if provided
        if (photos != null && !photos.isEmpty()) {
            try {
                List<String> photoUrls = photos.stream()
                        .map(storageService::uploadFile)
                        .collect(Collectors.toList());
                issue.setPhotoUrls(photoUrls);
            } catch (Exception e) {
                throw new RuntimeException("Error uploading photos: " + e.getMessage());
            }
        }

        // Save and return
        Issue savedIssue = issueRepository.save(issue);
        return convertToResponse(savedIssue);
    }

    @Override
    public List<IssueResponse> getAllIssues(int page, int size, String status) {
        try {
            if (status != null && !status.isEmpty()) {
                return issueRepository.findByStatus(status).stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList());
            }

            return issueRepository.findAll(PageRequest.of(page, size))
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public IssueResponse getIssueById(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + id));
        return convertToResponse(issue);
    }

    @Override
    @Transactional
    public IssueResponse updateIssue(Long id, IssueRequest request) {
        // Input validation
        validateIssueRequest(request);

        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + id));

        // Update fields
        issue.setDescription(request.getDescription());

        if (request.isUsingCustomLocation()) {
            issue.setCustomLocation(request.getCustomLocation());
            issue.setLatitude(null);
            issue.setLongitude(null);
        } else {
            issue.setLatitude(request.getLatitude());
            issue.setLongitude(request.getLongitude());
            issue.setCustomLocation(null);
        }
        issue.setUsingCustomLocation(request.isUsingCustomLocation());

        if ("Custom".equals(request.getCategory())) {
            issue.setCategory(request.getCustomCategory());
        } else {
            issue.setCategory(request.getCategory());
        }

        // Save and return
        Issue updatedIssue = issueRepository.save(issue);
        return convertToResponse(updatedIssue);
    }

    @Override
    @Transactional
    public void deleteIssue(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + id));

        // Delete associated photos first
        if (issue.getPhotoUrls() != null && !issue.getPhotoUrls().isEmpty()) {
            for (String photoUrl : issue.getPhotoUrls()) {
                try {
                    storageService.deleteFile(photoUrl);
                } catch (Exception e) {
                    // Log error but continue with deletion
                    System.err.println("Error deleting photo: " + photoUrl);
                }
            }
        }

        issueRepository.deleteById(id);
    }

    @Override
    public List<IssueResponse> getNearbyIssues(Double latitude, Double longitude, Double radiusKm) {
        if (latitude == null || longitude == null || radiusKm == null) {
            throw new IllegalArgumentException("Latitude, longitude, and radius are required");
        }

        try {
            double radiusMeters = radiusKm * 1000;
            return issueRepository.findNearbyIssues(latitude, longitude, radiusMeters)
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private IssueResponse convertToResponse(Issue issue) {
        IssueResponse response = new IssueResponse();
        response.setId(issue.getId());
        response.setDescription(issue.getDescription());
        response.setLatitude(issue.getLatitude());
        response.setLongitude(issue.getLongitude());
        response.setCustomLocation(issue.getCustomLocation());
        response.setUsingCustomLocation(issue.getUsingCustomLocation());
        response.setCategory(issue.getCategory());
        response.setStatus(issue.getStatus());
        response.setPhotoUrls(issue.getPhotoUrls() != null ? issue.getPhotoUrls() : new ArrayList<>());
        response.setCreatedAt(issue.getCreatedAt());
        response.setUpdatedAt(issue.getUpdatedAt());

        if (issue.getReportedBy() != null) {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(issue.getReportedBy().getId());
            userResponse.setUsername(issue.getReportedBy().getUsername());
            userResponse.setEmail(issue.getReportedBy().getEmail());
            response.setReportedBy(userResponse);
        }

        return response;
    }

    private void validateIssueRequest(IssueRequest request) {
        System.out.println("Validating request: " +
                "isUsingCustomLocation=" + request.isUsingCustomLocation() +
                ", latitude=" + request.getLatitude() +
                ", longitude=" + request.getLongitude() +
                ", customLocation=" + request.getCustomLocation());

        List<String> errors = new ArrayList<>();

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            errors.add("Description is required");
        }

        // Location validation with improved debugging
        if (request.isUsingCustomLocation()) {
            if (request.getCustomLocation() == null || request.getCustomLocation().trim().isEmpty()) {
                errors.add("Custom location is required when using custom location");
            }
            // For debugging - log when using custom location
            System.out.println("Using custom location: " + request.getCustomLocation());
        } else {
            if (request.getLatitude() == null || request.getLongitude() == null) {
                errors.add("Latitude and longitude are required when not using custom location");
                // For debugging - log the missing coordinates
                System.out.println("Missing coordinates - latitude: " + request.getLatitude() +
                        ", longitude: " + request.getLongitude());
            } else {
                // For debugging - log valid coordinates
                System.out.println("Valid coordinates received - latitude: " + request.getLatitude() +
                        ", longitude: " + request.getLongitude());
            }
        }

        // Category validation
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            errors.add("Category is required");
        } else if ("Custom".equals(request.getCategory()) &&
                (request.getCustomCategory() == null || request.getCustomCategory().trim().isEmpty())) {
            errors.add("Custom category description is required when using custom category");
        }

        if (!errors.isEmpty()) {
            String errorMessage = "Invalid issue request: " + String.join(", ", errors);
            System.out.println("Validation failed: " + errorMessage);
            throw new IllegalArgumentException(errorMessage);
        } else {
            System.out.println("Validation successful");
        }
    }
}