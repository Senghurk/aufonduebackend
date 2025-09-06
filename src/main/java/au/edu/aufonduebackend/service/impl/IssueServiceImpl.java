package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.model.dto.request.IssueRequest;
import au.edu.aufonduebackend.model.dto.response.IssueResponse;
import au.edu.aufonduebackend.model.dto.response.StaffResponse;
import au.edu.aufonduebackend.model.dto.response.UserResponse;
import au.edu.aufonduebackend.model.entity.Issue;
import au.edu.aufonduebackend.model.entity.IssueRemark;
import au.edu.aufonduebackend.model.entity.Staff;
import au.edu.aufonduebackend.model.entity.User;
import au.edu.aufonduebackend.repository.IssueRepository;
import au.edu.aufonduebackend.repository.StaffRepository;
import au.edu.aufonduebackend.service.IssueService;
import au.edu.aufonduebackend.service.IssueRemarkService;
import au.edu.aufonduebackend.service.StorageService;
import au.edu.aufonduebackend.service.UserService;
import au.edu.aufonduebackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private static final Logger logger = LoggerFactory.getLogger(IssueServiceImpl.class);
    
    private final IssueRepository issueRepository;
    private final StaffRepository staffRepository;
    private final StorageService storageService;
    private final UserService userService;
    @Autowired(required = false)
    private IssueRemarkService remarkService;
    @Autowired(required = false)
    private au.edu.aufonduebackend.repository.UpdateRepository updateRepository;
    @Autowired(required = false)
    private au.edu.aufonduebackend.repository.IssueRemarkRepository issueRemarkRepository;
    @Autowired(required = false)
    private au.edu.aufonduebackend.repository.IssueRemarkHistoryRepository issueRemarkHistoryRepository;

    @Override
    @Transactional
    public IssueResponse createIssue(IssueRequest request, List<MultipartFile> photos, List<MultipartFile> videos) {
        validateIssueRequest(request, photos, videos);

        // Get or create user from email
        String email = request.getUserEmail();
        String username = email.substring(0, email.indexOf("@")); // Extract username from email
        User user = userService.createUserAfterAuthentication(username, email);

        Issue issue = new Issue();
        issue.setDescription(request.getDescription());
        issue.setReportedBy(user);

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
        issue.setVideoUrls(new ArrayList<>());

        // Upload photos
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

        // Upload videos
        if (videos != null && !videos.isEmpty()) {
            try {
                List<String> videoUrls = videos.stream()
                        .map(storageService::uploadFile)
                        .collect(Collectors.toList());
                issue.setVideoUrls(videoUrls);
            } catch (Exception e) {
                throw new RuntimeException("Error uploading videos: " + e.getMessage());
            }
        }

        Issue savedIssue = issueRepository.save(issue);
        
        // Create initial 'new' remark for the issue if service is available
        if (remarkService != null) {
            remarkService.createInitialRemarkForNewIssue(savedIssue);
        }
        
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

        // First, delete all related entities to avoid foreign key constraint violations
        
        // Delete all updates associated with this issue
        if (updateRepository != null) {
            try {
                updateRepository.deleteByIssueId(id);
            } catch (Exception e) {
                System.err.println("Error deleting updates for issue " + id + ": " + e.getMessage());
            }
        }
        
        // Delete issue remark history
        if (issueRemarkHistoryRepository != null) {
            try {
                issueRemarkHistoryRepository.deleteByIssueId(id);
            } catch (Exception e) {
                System.err.println("Error deleting remark history for issue " + id + ": " + e.getMessage());
            }
        }
        
        // Delete issue remarks
        if (issueRemarkRepository != null) {
            try {
                issueRemarkRepository.deleteByIssue(issue);
            } catch (Exception e) {
                System.err.println("Error deleting remarks for issue " + id + ": " + e.getMessage());
            }
        }

        // Delete media files from storage
        if (issue.getPhotoUrls() != null && !issue.getPhotoUrls().isEmpty()) {
            for (String photoUrl : issue.getPhotoUrls()) {
                try {
                    storageService.deleteFile(photoUrl);
                } catch (Exception e) {
                    System.err.println("Error deleting photo: " + photoUrl);
                }
            }
        }

        if (issue.getVideoUrls() != null && !issue.getVideoUrls().isEmpty()) {
            for (String videoUrl : issue.getVideoUrls()) {
                try {
                    storageService.deleteFile(videoUrl);
                } catch (Exception e) {
                    System.err.println("Error deleting video: " + videoUrl);
                }
            }
        }

        // Finally, delete the issue itself
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
        response.setVideoUrls(issue.getVideoUrls() != null ? issue.getVideoUrls() : new ArrayList<>());
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
        
        // Add remark information if service is available
        if (remarkService != null) {
            remarkService.getRemarkByIssueId(issue.getId()).ifPresent(remark -> {
                response.setRemarkType(remark.getRemarkType().getValue());
                response.setRemarkViewed(remark.getIsViewed());
            });
        }

        return response;
    }

    private void validateIssueRequest(IssueRequest request) {
        List<String> errors = new ArrayList<>();

        // Description is required
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            errors.add("Description is required");
        }

        // User email is required (but no domain restriction)
        if (request.getUserEmail() == null || request.getUserEmail().trim().isEmpty()) {
            errors.add("User email is required");
        }

        // Custom location is required (since app always uses custom location)
        if (request.getCustomLocation() == null || request.getCustomLocation().trim().isEmpty()) {
            errors.add("Location description is required");
        }

        // Category is required
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            errors.add("Category is required");
        } else if ("Custom".equals(request.getCategory()) &&
                (request.getCustomCategory() == null || request.getCustomCategory().trim().isEmpty())) {
            errors.add("Custom category description is required when using custom category");
        }

        // Note: Photo validation is handled separately in the controller/service layer
        // since photos are uploaded as multipart files

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Invalid issue request: " + String.join(", ", errors));
        }
    }

    private void validateIssueRequest(IssueRequest request, List<MultipartFile> photos, List<MultipartFile> videos) {
        // Validate basic request fields
        validateIssueRequest(request);

        // Additional validation for media files
        List<String> errors = new ArrayList<>();

        // Check if at least one media file is provided
        boolean hasPhotos = photos != null && !photos.isEmpty();
        boolean hasVideos = videos != null && !videos.isEmpty();
        
        if (!hasPhotos && !hasVideos) {
            errors.add("At least one photo or video must be provided");
        }

        // Validate video file size (100MB limit)
        if (videos != null && !videos.isEmpty()) {
            long maxVideoSize = 100 * 1024 * 1024L; // 100MB in bytes
            
            for (MultipartFile video : videos) {
                if (video.getSize() > maxVideoSize) {
                    errors.add("Video file '" + video.getOriginalFilename() + "' exceeds 100MB limit");
                }
                
                // Check file type
                String contentType = video.getContentType();
                if (contentType != null && !contentType.startsWith("video/")) {
                    errors.add("File '" + video.getOriginalFilename() + "' is not a valid video file");
                }
            }
        }

        // Validate photo files
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                String contentType = photo.getContentType();
                if (contentType != null && !contentType.startsWith("image/")) {
                    errors.add("File '" + photo.getOriginalFilename() + "' is not a valid image file");
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Invalid media files: " + String.join(", ", errors));
        }
    }

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
    public List<IssueResponse> getAssignedIssuesByStaff(Long staffId, int page, int size) {
        try {
            logger.info("Fetching assigned issues for staff ID: {}", staffId);
            List<Issue> assignedIssues = issueRepository.findByAssignedTrueAndAssignedToId(staffId);
            logger.info("Found {} assigned issues for staff ID: {}", assignedIssues.size(), staffId);
            return assignedIssues.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching assigned issues for staff ID: " + staffId, e);
            return new ArrayList<>();
        }
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