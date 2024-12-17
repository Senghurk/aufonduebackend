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
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setLatitude(request.getLatitude());
        issue.setLongitude(request.getLongitude());
        issue.setLocation(request.getLocation());
        issue.setCategory(request.getCategory());
        issue.setPriority(request.getPriority());
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
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setLatitude(request.getLatitude());
        issue.setLongitude(request.getLongitude());
        issue.setLocation(request.getLocation());
        issue.setCategory(request.getCategory());
        issue.setPriority(request.getPriority());

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
        response.setTitle(issue.getTitle());
        response.setDescription(issue.getDescription());
        response.setLatitude(issue.getLatitude());
        response.setLongitude(issue.getLongitude());
        response.setLocation(issue.getLocation());
        response.setCategory(issue.getCategory());
        response.setPriority(issue.getPriority());
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
        List<String> errors = new ArrayList<>();

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            errors.add("Title is required");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            errors.add("Description is required");
        }
        if (request.getLatitude() == null) {
            errors.add("Latitude is required");
        }
        if (request.getLongitude() == null) {
            errors.add("Longitude is required");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Invalid issue request: " + String.join(", ", errors));
        }
    }
}