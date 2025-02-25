package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.model.dto.request.UpdateRequest;
import au.edu.aufonduebackend.model.dto.response.UpdateResponse;
import au.edu.aufonduebackend.model.entity.Issue;
import au.edu.aufonduebackend.model.entity.Update;
import au.edu.aufonduebackend.repository.IssueRepository;
import au.edu.aufonduebackend.repository.UpdateRepository;
import au.edu.aufonduebackend.service.StorageService;
import au.edu.aufonduebackend.service.UpdateService;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
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

    private static final List<String> VALID_STATUSES = Arrays.asList("PENDING", "IN PROGRESS", "COMPLETED");

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
        update.setStatus(request.getStatus());  // This will also update issue status
        update.setComment(request.getComment());
        update.setPhotoUrls(uploadedPhotoUrls);

        issueRepository.save(issue);  // Save issue with new status
        update = updateRepository.save(update); // Save the update

        // Return as UpdateResponse DTO
        return new UpdateResponse(update.getId(), issue.getId(), update.getStatus(), update.getComment(), update.getUpdateTime(), update.getPhotoUrls());
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
                        update.getPhotoUrls()
                ))
                .toList();
    }




}
