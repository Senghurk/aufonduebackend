package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.model.dto.request.IssueRequest;
import au.edu.aufonduebackend.model.dto.response.IssueResponse;
import au.edu.aufonduebackend.model.dto.response.StaffResponse;
import au.edu.aufonduebackend.model.dto.response.UserResponse;
import au.edu.aufonduebackend.model.entity.Issue;
import au.edu.aufonduebackend.model.entity.Staff;
import au.edu.aufonduebackend.model.entity.User;
import au.edu.aufonduebackend.repository.IssueRepository;
import au.edu.aufonduebackend.repository.StaffRepository;
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
    private final StaffRepository staffRepository;
    private final StorageService storageService;

    @Override
    @Transactional
    public IssueResponse createIssue(IssueRequest request, List<MultipartFile> photos) {
        validateIssueRequest(request);

        Issue issue = new Issue();
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

        issue.setStatus("PENDING");
        issue.setPhotoUrls(new ArrayList<>());

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

        Issue savedIssue = issueRepository.save(issue);
        return convertToResponse(savedIssue);
    }

    @Override
    public List<IssueResponse> getUserSubmittedIssues(Long userId, int page, int size, String status) {
        try {
            return issueRepository.findUserIssues(userId, status, page * size, size)
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<IssueResponse> getAllIssuesTracking(int page, int size, String status) {
        try {
            return issueRepository.findAllIssues(status, page * size, size)
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
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
        validateIssueRequest(request);

        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + id));

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

        Issue updatedIssue = issueRepository.save(issue);
        return convertToResponse(updatedIssue);
    }

    @Override
    @Transactional
    public void deleteIssue(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + id));

        if (issue.getPhotoUrls() != null && !issue.getPhotoUrls().isEmpty()) {
            for (String photoUrl : issue.getPhotoUrls()) {
                try {
                    storageService.deleteFile(photoUrl);
                } catch (Exception e) {
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
        response.setAssigned(issue.getAssigned());
        response.setPriority(issue.getPriority());

        if (issue.getReportedBy() != null) {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(issue.getReportedBy().getId());
            userResponse.setUsername(issue.getReportedBy().getUsername());
            userResponse.setEmail(issue.getReportedBy().getEmail());
            response.setReportedBy(userResponse);
        }

        if (issue.getAssignedTo() != null) {
            StaffResponse staffResponse = new StaffResponse();
            staffResponse.setId(issue.getAssignedTo().getId());
            staffResponse.setName(issue.getAssignedTo().getName());
            staffResponse.setEmail(issue.getAssignedTo().getEmail());
            response.setAssignedTo(staffResponse);
        }

        return response;
    }

    private void validateIssueRequest(IssueRequest request) {
        List<String> errors = new ArrayList<>();

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            errors.add("Description is required");
        }

        if (request.isUsingCustomLocation()) {
            if (request.getCustomLocation() == null || request.getCustomLocation().trim().isEmpty()) {
                errors.add("Custom location is required when using custom location");
            }
        } else {
            if (request.getLatitude() == null || request.getLongitude() == null) {
                errors.add("Latitude and longitude are required when not using custom location");
            }
        }

        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            errors.add("Category is required");
        } else if ("Custom".equals(request.getCategory()) &&
                (request.getCustomCategory() == null || request.getCustomCategory().trim().isEmpty())) {
            errors.add("Custom category description is required when using custom category");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Invalid issue request: " + String.join(", ", errors));
        }
    }



    //TEST EDIT BY MATT







    @Override
    public void assignIssueToStaff(Long issueId, Long staffId, String priority) {
        // Fetch the issue by ID
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));

        // Fetch the staff by ID
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + staffId));

        // Update the issue
        issue.setAssigned(true);
        issue.setAssignedTo(staff);
        if (priority != null) {
            priority = priority.toUpperCase();
            issue.setPriority(priority);
        }
        issueRepository.save(issue); // Save the updated issue
    }

    @Override
    public List<IssueResponse> getUnassignedIssues(int page, int size) {
        List<Issue> unassignedIssues = issueRepository.findByAssignedFalse();
        return unassignedIssues.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<IssueResponse> getAssignedIssues(int page, int size) {
        List<Issue> assignedIssues = issueRepository.findByAssignedTrue();
        return assignedIssues.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public IssueResponse getUnassignedIssueByID(Long issueId) {
        Issue issue = issueRepository.getUnassignedIssueByID(issueId);
        return convertToResponse(issue);
    }

    @Override
    public List<IssueResponse> getCompletedIssues() {
        List<Issue> completedIssues = issueRepository.findCompletedIssues("completed");

        // Convert entities to DTOs if needed
        return completedIssues.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }




}