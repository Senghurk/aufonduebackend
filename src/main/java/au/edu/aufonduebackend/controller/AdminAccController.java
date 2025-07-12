package au.edu.aufonduebackend.controller;

import au.edu.aufonduebackend.model.dto.response.AdminResponse;
import au.edu.aufonduebackend.model.entity.Admin;
import au.edu.aufonduebackend.repository.AdminRepository;
import au.edu.aufonduebackend.service.AdminService;
import au.edu.aufonduebackend.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminAccController {

    @Autowired
    public AdminService adminService;


    // Check if email exists in admin table
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAdminEmail(@RequestParam String email) {
        boolean allowed = adminService.isAdminEmailAllowed(email);
        return ResponseEntity.ok(allowed);
    }

    // Endpoint to add a new admin
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
}