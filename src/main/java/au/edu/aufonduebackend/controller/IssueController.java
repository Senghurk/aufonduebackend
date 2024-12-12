package au.edu.aufonduebackend.controller;

import au.edu.aufonduebackend.model.dto.request.IssueRequest;
import au.edu.aufonduebackend.model.dto.response.IssueResponse;
import au.edu.aufonduebackend.model.dto.response.ApiResponse;
import au.edu.aufonduebackend.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
@Tag(name = "Issues", description = "Maintenance Issue Management APIs")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    @Operation(summary = "Report a new maintenance issue")
    public ResponseEntity<ApiResponse<IssueResponse>> createIssue(
            @Valid @RequestPart("issue") IssueRequest request,
            @RequestPart("photos") List<MultipartFile> photos) {
        IssueResponse response = issueService.createIssue(request, photos);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Issue reported successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all maintenance issues")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getAllIssues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        List<IssueResponse> issues = issueService.getAllIssues(page, size, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Issues retrieved successfully", issues));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get issue by ID")
    public ResponseEntity<ApiResponse<IssueResponse>> getIssueById(@PathVariable Long id) {
        IssueResponse issue = issueService.getIssueById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Issue retrieved successfully", issue));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update issue status")
    public ResponseEntity<ApiResponse<IssueResponse>> updateIssueStatus(
            @PathVariable Long id,
            @Valid @RequestBody IssueRequest request) {
        IssueResponse response = issueService.updateIssue(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Issue updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an issue")
    public ResponseEntity<ApiResponse<Void>> deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Issue deleted successfully", null));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Get nearby issues")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getNearbyIssues(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "1.0") Double radiusKm) {
        List<IssueResponse> issues = issueService.getNearbyIssues(latitude, longitude, radiusKm);
        return ResponseEntity.ok(new ApiResponse<>(true, "Nearby issues retrieved successfully", issues));
    }
}