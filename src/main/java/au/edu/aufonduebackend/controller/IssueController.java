package au.edu.aufonduebackend.controller;

import au.edu.aufonduebackend.model.dto.request.IssueRequest;
import au.edu.aufonduebackend.model.dto.response.ApiResponse;
import au.edu.aufonduebackend.model.dto.response.IssueResponse;
import au.edu.aufonduebackend.service.IssueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;


// API endpoints for users to create and view issues

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse<IssueResponse>> createIssue(
            @RequestPart("issue") String issueJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            @RequestPart(value = "videos", required = false) List<MultipartFile> videos) {
        try {
            IssueRequest request = objectMapper.readValue(issueJson, IssueRequest.class);
            IssueResponse response = issueService.createIssue(request, photos, videos);
            return ResponseEntity.ok(ApiResponse.success(response, "Issue created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error creating issue: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/submitted")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getUserSubmittedIssues(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        List<IssueResponse> issues = issueService.getUserSubmittedIssues(userId, page, size, status);
        return ResponseEntity.ok(ApiResponse.success(issues, "User submitted issues retrieved successfully"));
    }

    @GetMapping("/tracking")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getAllIssuesTracking(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        List<IssueResponse> issues = issueService.getAllIssuesTracking(page, size, status);
        return ResponseEntity.ok(ApiResponse.success(issues, "All tracked issues retrieved successfully"));
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
        try {
            IssueResponse issue = issueService.getIssueById(id);
            return ResponseEntity.ok(ApiResponse.success(issue, "Issue retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IssueResponse>> updateIssue(
            @PathVariable Long id,
            @RequestBody IssueRequest request) {
        try {
            IssueResponse updated = issueService.updateIssue(id, request);
            return ResponseEntity.ok(ApiResponse.success(updated, "Issue updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIssue(@PathVariable Long id) {
        try {
            issueService.deleteIssue(id);
            return ResponseEntity.ok(ApiResponse.<Void>success(null, "Issue deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getNearbyIssues(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double radiusKm) {
        try {
            List<IssueResponse> issues = issueService.getNearbyIssues(latitude, longitude, radiusKm);
            return ResponseEntity.ok(ApiResponse.success(issues, "Nearby issues retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}