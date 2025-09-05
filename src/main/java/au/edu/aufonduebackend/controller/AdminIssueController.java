package au.edu.aufonduebackend.controller;


import au.edu.aufonduebackend.model.dto.request.UpdateRequest;
import au.edu.aufonduebackend.model.dto.response.IssueResponse;
import au.edu.aufonduebackend.model.dto.response.UpdateResponse;
import au.edu.aufonduebackend.model.entity.Admin;
import au.edu.aufonduebackend.repository.IssueRepository;
import au.edu.aufonduebackend.repository.AdminRepository;
import au.edu.aufonduebackend.service.IssueService;
import au.edu.aufonduebackend.service.IssueRemarkService;
import au.edu.aufonduebackend.service.StaffService;
import au.edu.aufonduebackend.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Admin-specific endpoints for issue management

@Controller
@RequestMapping("/api/issues")
public class AdminIssueController {
    @Autowired
    public IssueService issueService;
    
    @Autowired(required = false)
    public StaffService staffService;

    @Autowired
    public UpdateService updateService;
    
    @Autowired(required = false)
    public IssueRemarkService remarkService;
    
    @Autowired(required = false)
    public AdminRepository adminRepository;

    @Autowired
    public IssueRepository issueRepository;

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
            @RequestParam Long staffId,
            @RequestParam(required = false) String priority
    ) {
        issueService.assignIssueToStaff(issueId, staffId, priority);
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



    @GetMapping("/{issueId}/updates")
    public ResponseEntity<List<UpdateResponse>> getUpdatesByIssue(@PathVariable Long issueId) {
        List<UpdateResponse> updates = updateService.getUpdatesByIssueId(issueId);
        return ResponseEntity.ok(updates);
    }


    @GetMapping("/completed")
    public ResponseEntity<List<IssueResponse>> getCompletedIssues() {
        return ResponseEntity.ok(issueService.getCompletedIssues());
    }


    // to get issue stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getIssueStats() {
        long totalIssues = issueRepository.count();
        long pendingIssues = issueRepository.countByStatus("PENDING");
        long inProgressIssues = issueRepository.countByStatus("IN PROGRESS");
        long completedIssues = issueRepository.countByStatus("COMPLETED");

        long incompleteIssues = pendingIssues + inProgressIssues;

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalIssues", totalIssues);
        stats.put("incompleteIssues", incompleteIssues);
        stats.put("completedIssues", completedIssues);

        return ResponseEntity.ok(stats);
    }
    
    // Mark 'new' remark as viewed when details button is clicked
    @PostMapping("/{issueId}/remark/view")
    public ResponseEntity<String> markRemarkAsViewed(
            @PathVariable Long issueId,
            @RequestParam(required = false) String adminEmail) {
        if (remarkService == null) {
            // If remark service is not available, return success anyway
            return ResponseEntity.ok("Remark service not available");
        }
        
        try {
            Admin viewedBy = null;
            if (adminEmail != null && adminRepository != null) {
                viewedBy = adminRepository.findByEmail(adminEmail).orElse(null);
            }
            
            remarkService.markRemarkAsViewed(issueId, viewedBy);
            return ResponseEntity.ok("Remark marked as viewed");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error marking remark as viewed: " + e.getMessage());
        }
    }

}
