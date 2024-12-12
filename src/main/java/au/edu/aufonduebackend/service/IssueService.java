// File: src/main/java/com/aufondue/service/IssueService.java

package au.edu.aufonduebackend.service;

import au.edu.aufonduebackend.model.dto.request.IssueRequest;
import au.edu.aufonduebackend.model.dto.response.IssueResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface IssueService {
    IssueResponse createIssue(IssueRequest request, List<MultipartFile> photos);
    List<IssueResponse> getAllIssues(int page, int size, String status);
    IssueResponse getIssueById(Long id);
    IssueResponse updateIssue(Long id, IssueRequest request);
    void deleteIssue(Long id);
    List<IssueResponse> getNearbyIssues(Double latitude, Double longitude, Double radiusKm);
}