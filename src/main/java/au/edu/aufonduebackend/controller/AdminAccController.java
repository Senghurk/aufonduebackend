package au.edu.aufonduebackend.controller;

import au.edu.aufonduebackend.model.dto.response.AdminResponse;
import au.edu.aufonduebackend.model.entity.Admin;
import au.edu.aufonduebackend.repository.AdminRepository;
import au.edu.aufonduebackend.repository.IssueRepository;
import au.edu.aufonduebackend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminAccController {

    @Autowired
    public AdminService adminService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private IssueRepository issueRepository;

    @GetMapping("/details")
    public ResponseEntity<?> getAdminDetails(@RequestParam String email) {
        Optional<Admin> adminOpt = adminService.findByEmail(email);

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("id", admin.getId());
            response.put("username", admin.getUsername());
            response.put("email", admin.getEmail());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
        }
    }

    @PostMapping
    public ResponseEntity<AdminResponse> addAdmin(@RequestBody Admin admin) {
        AdminResponse createdAdmin = adminService.addAdmin(admin);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AdminResponse>> getAllAdmins() {
        List<AdminResponse> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }


    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAdminEmail(@RequestParam String email) {
        Optional<Admin> adminOptional = adminService.findByEmail(email);
        boolean isAdmin = adminOptional.isPresent();
        return ResponseEntity.ok(isAdmin);
    }

    // Dashboard stats endpoint
    @GetMapping("/issues/stats")
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



}
