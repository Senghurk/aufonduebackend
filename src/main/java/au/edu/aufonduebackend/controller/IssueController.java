// File: src/main/java/au/edu/aufonduebackend/controller/IssueController.java

package au.edu.aufonduebackend.controller;

import au.edu.aufonduebackend.model.dto.request.IssueRequest;
import au.edu.aufonduebackend.model.dto.response.ApiResponse;
import au.edu.aufonduebackend.model.dto.response.IssueResponse;
import au.edu.aufonduebackend.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin
public class IssueController {

    @Autowired
    private IssueService issueService;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse<IssueResponse>> createIssue(
            @RequestPart("issue") IssueRequest request,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
        IssueResponse response = issueService.createIssue(request, photos);
        return ResponseEntity.ok(ApiResponse.success(response, "Issue created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getAllIssues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        List<IssueResponse> issues = issueService.getAllIssues(page, size, status);
        return ResponseEntity.ok(ApiResponse.success(issues, "Issues retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IssueResponse>> getIssueById(@PathVariable Long id) {
        IssueResponse issue = issueService.getIssueById(id);
        return ResponseEntity.ok(ApiResponse.success(issue, "Issue retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IssueResponse>> updateIssue(
            @PathVariable Long id,
            @RequestBody IssueRequest request) {
        IssueResponse updated = issueService.updateIssue(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Issue updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null, "Issue deleted successfully"));
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getNearbyIssues(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double radiusKm) {
        List<IssueResponse> issues = issueService.getNearbyIssues(latitude, longitude, radiusKm);
        return ResponseEntity.ok(ApiResponse.success(issues, "Nearby issues retrieved successfully"));
    }
}