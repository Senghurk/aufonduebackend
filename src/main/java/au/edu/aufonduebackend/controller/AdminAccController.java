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
    public ResponseEntity<?> addAdmin(@RequestBody Admin admin) {
        // Validate email domain
        if (admin.getEmail() == null || !admin.getEmail().toLowerCase().endsWith("@au.edu")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid email domain");
            error.put("message", "Admin email must be an @au.edu address");
            return ResponseEntity.badRequest().body(error);
        }
        
        // Check if admin with this email already exists
        Optional<Admin> existingAdmin = adminService.findByEmail(admin.getEmail());
        if (existingAdmin.isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Admin already exists");
            error.put("message", "An admin with this email already exists");
            return ResponseEntity.badRequest().body(error);
        }
        
        // Set a temporary username that will be replaced with Microsoft display name on first login
        // If username is provided, use it as a placeholder, otherwise use email prefix
        if (admin.getUsername() == null || admin.getUsername().trim().isEmpty()) {
            String emailPrefix = admin.getEmail().substring(0, admin.getEmail().indexOf("@"));
            admin.setUsername(emailPrefix);
        }
        
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        try {
            Optional<Admin> adminOpt = adminRepository.findById(id);
            
            if (!adminOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Admin not found");
                error.put("message", "Admin with ID " + id + " does not exist");
                return ResponseEntity.notFound().build();
            }
            
            Admin admin = adminOpt.get();
            
            // Prevent deleting the last admin
            long adminCount = adminRepository.count();
            if (adminCount <= 1) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Cannot delete last admin");
                error.put("message", "System must have at least one admin");
                return ResponseEntity.badRequest().body(error);
            }
            
            adminRepository.deleteById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Admin " + admin.getUsername() + " has been deleted successfully");
            response.put("deletedAdmin", admin.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Delete failed");
            error.put("message", "Failed to delete admin: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

}
