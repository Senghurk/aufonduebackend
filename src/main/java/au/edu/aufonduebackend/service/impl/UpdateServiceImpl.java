package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.model.dto.request.UpdateRequest;
import au.edu.aufonduebackend.model.dto.response.UpdateResponse;
import au.edu.aufonduebackend.model.entity.Issue;
import au.edu.aufonduebackend.model.entity.Update;
import au.edu.aufonduebackend.model.entity.User;
import au.edu.aufonduebackend.repository.IssueRepository;
import au.edu.aufonduebackend.repository.UpdateRepository;
import au.edu.aufonduebackend.service.StorageService;
import au.edu.aufonduebackend.service.UpdateService;
import au.edu.aufonduebackend.service.FcmService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateServiceImpl implements UpdateService {

    private final UpdateRepository updateRepository;
    private final IssueRepository issueRepository;
    private final StorageService storageService;
    private final FcmService fcmService; // ADD THIS

    private static final Logger logger = LoggerFactory.getLogger(UpdateServiceImpl.class);
    private static final List<String> VALID_STATUSES = Arrays.asList("PENDING", "IN_PROGRESS", "COMPLETED");

    @Override
    @Transactional
    public UpdateResponse createUpdate(UpdateRequest request, List<MultipartFile> photos) {
        Issue issue = issueRepository.findById(request.getIssueId())
                .orElseThrow(() -> new EntityNotFoundException("Issue not found"));

        // Validate status
        if (!VALID_STATUSES.contains(request.getStatus().toUpperCase())) {
            throw new IllegalArgumentException("Invalid status: " + request.getStatus());
        }

        // Upload photos and collect URLs
        List<String> uploadedPhotoUrls = (photos != null) ?
                photos.stream().map(storageService::uploadFile).collect(Collectors.toList()) : List.of();

        // Create and save update record
        Update update = new Update();
        update.setIssue(issue);
        update.setStatus(request.getStatus().toUpperCase());  // This will also update issue status
        update.setComment(request.getComment());
        update.setPhotoUrls(uploadedPhotoUrls);

        // Save issue and update
        issueRepository.save(issue);  // Save issue with new status
        update = updateRepository.save(update); // Save the update

        // SEND FCM NOTIFICATION
        boolean notificationSent = false;
        String notificationError = null;

        try {
            // Get the user who reported the issue
            User reporter = issue.getReportedBy();

            if (reporter != null && reporter.getFcmToken() != null) {
                notificationSent = fcmService.sendIssueUpdateNotification(
                        reporter.getFcmToken(),
                        issue.getId(),
                        request.getStatus(),
                        request.getComment()
                );

                if (notificationSent) {
                    logger.info("Push notification sent successfully for issue {}", issue.getId());
                } else {
                    logger.warn("Failed to send push notification for issue {}", issue.getId());
                    notificationError = "FCM service returned false";
                }
            } else {
                logger.warn("No FCM token available for user who reported issue {}", issue.getId());
                notificationError = "User FCM token not available";
            }
        } catch (Exception e) {
            logger.error("Failed to send push notification for issue {}", issue.getId(), e);
            notificationError = e.getMessage();
        }

        // Return as UpdateResponse DTO with notification status
        return new UpdateResponse(
                update.getId(),
                issue.getId(),
                update.getStatus(),
                update.getComment(),
                update.getUpdateTime(),
                update.getPhotoUrls(),
                notificationSent,
                notificationError
        );
    }

    @Override
    public List<UpdateResponse> getUpdatesByIssueId(Long issueId) {
        List<Update> updates = updateRepository.findByIssueId(issueId);

        return updates.stream()
                .map(update -> new UpdateResponse(
                        update.getId(),
                        update.getIssue().getId(),
                        update.getStatus(),
                        update.getComment(),
                        update.getUpdateTime(),
                        update.getPhotoUrls(),
                        null, // No notification status for historical updates
                        null
                ))
                .toList();
    }
}