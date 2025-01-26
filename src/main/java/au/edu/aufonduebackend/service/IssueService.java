package au.edu.aufonduebackend.service;

import au.edu.aufonduebackend.model.dto.request.IssueRequest;
import au.edu.aufonduebackend.model.dto.response.IssueResponse;
import au.edu.aufonduebackend.model.entity.Issue;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface IssueService {
    // Create and modify operations
    IssueResponse createIssue(IssueRequest request, List<MultipartFile> photos);
    IssueResponse updateIssue(Long id, IssueRequest request);
    void deleteIssue(Long id);

    // Query operations
    List<IssueResponse> getAllIssues(int page, int size, String status);
    List<IssueResponse> getUserSubmittedIssues(Long userId, int page, int size, String status);
    List<IssueResponse> getAllIssuesTracking(int page, int size, String status);
    IssueResponse getIssueById(Long id);
    List<IssueResponse> getNearbyIssues(Double latitude, Double longitude, Double radiusKm);


    // TEST EDIT BY MATT
    void addMockData(); // For adding mock data
    void assignIssueToStaff(Long issueId, Long staffId);

    List<IssueResponse> getUnassignedIssues(int page, int size);

    List<IssueResponse> getAssignedIssues(int page, int size);
    IssueResponse getUnassignedIssueByID(Long id);





}