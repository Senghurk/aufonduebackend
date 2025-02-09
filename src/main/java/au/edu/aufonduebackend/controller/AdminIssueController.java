package au.edu.aufonduebackend.controller;


import au.edu.aufonduebackend.model.dto.request.UpdateRequest;
import au.edu.aufonduebackend.model.dto.response.ApiResponse;
import au.edu.aufonduebackend.model.dto.response.IssueResponse;
import au.edu.aufonduebackend.model.dto.response.UpdateResponse;
import au.edu.aufonduebackend.model.entity.Issue;
import au.edu.aufonduebackend.model.entity.Staff;
import au.edu.aufonduebackend.model.entity.Update;
import au.edu.aufonduebackend.model.entity.User;
import au.edu.aufonduebackend.repository.IssueRepository;
import au.edu.aufonduebackend.repository.UpdateRepository;
import au.edu.aufonduebackend.service.IssueService;
import au.edu.aufonduebackend.service.StaffService;
import au.edu.aufonduebackend.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Controller
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/issues")
public class AdminIssueController {
    @Autowired
    public IssueService issueService;
    public StaffService staffService;

    @Autowired
    public UpdateService updateService;

    public UpdateRepository updateRepository;

    @GetMapping("/reports")
    public ResponseEntity<List<IssueResponse>> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        // Fetch the issues via the service method
        List<IssueResponse> issues = issueService.getAllIssues(page, size, status);

        // Return the fetched issues with HTTP status 200 OK
        return ResponseEntity.ok(issues);
    }

    @PostMapping("/reports")
    public ResponseEntity<String> addMockData() {
        // Use the service to add mock data
        issueService.addMockData();
        return ResponseEntity.status(HttpStatus.CREATED).body("Mock data added successfully!");
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        try {
            // Delete the issue using the service method
            issueService.deleteIssue(id);

            // Return a success response with HTTP status 204 No Content (indicating successful deletion)
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Handle the exception (e.g., issue not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    // Endpoint to get all unassigned issues
    @GetMapping("/unassigned")
    public ResponseEntity<List<IssueResponse>> getUnassignedIssues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<IssueResponse> unassignedIssues = issueService.getUnassignedIssues(page, size);
        return ResponseEntity.ok(unassignedIssues);
    }


    // Endpoint to get all assigned issues
    @GetMapping("/assigned")
    public ResponseEntity<List<IssueResponse>> getAssignedIssues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<IssueResponse> assignedIssues = issueService.getAssignedIssues(page, size);
        return ResponseEntity.ok(assignedIssues);
    }


    @PostMapping("/{issueId}/assign")
    public ResponseEntity<String> assignIssueToStaff(
            @PathVariable Long issueId,
            @RequestParam Long staffId
    ) {
        issueService.assignIssueToStaff(issueId, staffId);
        return ResponseEntity.ok("Issue assigned successfully.");
    }

    @GetMapping("/unassigned/{id}")
    public ResponseEntity<IssueResponse> getUnassignedIssueByID(@PathVariable Long id) {
        IssueResponse unassignedIssue = issueService.getUnassignedIssueByID(id);
        return ResponseEntity.ok(unassignedIssue);
    }





    // For admin updating process
    @PostMapping("updates")
    public ResponseEntity<UpdateResponse> createUpdate(
            @ModelAttribute UpdateRequest request,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos) {

        UpdateResponse response = updateService.createUpdate(request, photos);
        return ResponseEntity.ok(response);
    }

    @PutMapping("updates/{updateId}/status")
    public ResponseEntity<UpdateResponse> changeUpdateStatus(
            @PathVariable Long updateId,
            @RequestParam String newStatus) {

        UpdateResponse response = updateService.changeUpdateStatus(updateId, newStatus);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{issueId}/updates")
    public ResponseEntity<List<UpdateResponse>> getUpdatesByIssue(@PathVariable Long issueId) {
        List<UpdateResponse> updates = updateService.getUpdatesByIssueId(issueId);
        return ResponseEntity.ok(updates);
    }
}
