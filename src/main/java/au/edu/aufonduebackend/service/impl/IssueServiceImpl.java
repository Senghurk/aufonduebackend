// File: src/main/java/com/aufondue/service/impl/IssueServiceImpl.java

package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.model.dto.request.IssueRequest;
import au.edu.aufonduebackend.model.dto.response.IssueResponse;
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
        Issue issue = new Issue();
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setLatitude(request.getLatitude());
        issue.setLongitude(request.getLongitude());
        issue.setLocation(request.getLocation());
        issue.setCategory(request.getCategory());
        issue.setPriority(request.getPriority());

        // Upload photos and get URLs
        List<String> photoUrls = photos.stream()
                .map(storageService::uploadFile)
                .collect(Collectors.toList());
        issue.setPhotoUrls(photoUrls);

        Issue savedIssue = issueRepository.save(issue);
        return convertToResponse(savedIssue);
    }

    @Override
    public List<IssueResponse> getAllIssues(int page, int size, String status) {
        if (status != null) {
            return issueRepository.findByStatus(status).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }

        return issueRepository.findAll(PageRequest.of(page, size))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
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
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + id));

        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setPriority(request.getPriority());

        Issue updatedIssue = issueRepository.save(issue);
        return convertToResponse(updatedIssue);
    }

    @Override
    @Transactional
    public void deleteIssue(Long id) {
        if (!issueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Issue not found with id: " + id);
        }
        issueRepository.deleteById(id);
    }

    @Override
    public List<IssueResponse> getNearbyIssues(Double latitude, Double longitude, Double radiusKm) {
        double radiusMeters = radiusKm * 1000;
        return issueRepository.findNearbyIssues(latitude, longitude, radiusMeters)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
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
        response.setPhotoUrls(issue.getPhotoUrls());
        response.setCreatedAt(issue.getCreatedAt());
        response.setUpdatedAt(issue.getUpdatedAt());
        response.setReportedBy(issue.getReportedBy() != null ? issue.getReportedBy().getUsername() : null);
        return response;
    }
}