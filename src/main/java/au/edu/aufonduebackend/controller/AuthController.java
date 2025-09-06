package au.edu.aufonduebackend.controller;

import au.edu.aufonduebackend.model.entity.Staff;
import au.edu.aufonduebackend.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "https://au-fondue-web.vercel.app"})
public class AuthController {
    
    @Autowired
    private StaffService staffService;
    
    @Autowired(required = false)
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String accessToken = request.get("accessToken");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // TODO: Implement proper admin authentication with Microsoft OAuth
            // For now, we'll validate against a hardcoded admin list
            // In production, this should verify the Microsoft OAuth token
            
            if (email == null || !email.endsWith("@au.edu")) {
                response.put("success", false);
                response.put("message", "Invalid email domain. Admin must use @au.edu email");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Hardcoded admin accounts for testing
            // Replace with database lookup in production
            List<String> allowedAdmins = List.of(
                "u6440041@au.edu",
                "admin@au.edu",
                "test.admin@au.edu"
            );
            
            if (!allowedAdmins.contains(email.toLowerCase())) {
                response.put("success", false);
                response.put("message", "You are not authorized as an admin");
                return ResponseEntity.status(403).body(response);
            }
            
            // Success
            response.put("success", true);
            response.put("message", "Admin login successful");
            
            Map<String, Object> data = new HashMap<>();
            data.put("userType", "admin");
            data.put("userId", 1L);
            data.put("name", email.split("@")[0]);
            data.put("email", email);
            data.put("firstLogin", false);
            data.put("token", "admin-token-" + System.currentTimeMillis());
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Authentication failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/staff/login")
    public ResponseEntity<?> staffLogin(@RequestBody Map<String, String> request) {
        String omId = request.get("omId");
        String password = request.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find staff by staff ID
            Staff staff = staffService.findByStaffId(omId);
            
            // Check if staff exists
            if (staff == null) {
                response.put("success", false);
                response.put("message", "Invalid staff ID or password");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Verify password
            boolean passwordMatches = false;
            if (staff.getPassword() != null) {
                if (passwordEncoder != null) {
                    passwordMatches = passwordEncoder.matches(password, staff.getPassword());
                } else {
                    // Fallback to simple comparison if encoder not available (for testing only)
                    passwordMatches = password.equals(staff.getPassword());
                }
            }
            
            // Check for default password
            if (!passwordMatches && "OMstaff123".equals(password)) {
                passwordMatches = true;
            }
            
            if (!passwordMatches) {
                response.put("success", false);
                response.put("message", "Invalid staff ID or password");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Update last login time
            staff.setUpdatedAt(LocalDateTime.now());
            
            // Successful login
            response.put("success", true);
            response.put("message", "Staff login successful");
            
            Map<String, Object> data = new HashMap<>();
            data.put("userType", "staff");
            data.put("userId", staff.getId());
            data.put("staffId", staff.getStaffId());
            data.put("name", staff.getName());
            data.put("email", staff.getEmail());
            data.put("firstLogin", staff.getFirstLogin() != null ? staff.getFirstLogin() : false);
            data.put("token", "staff-token-" + staff.getId());
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/staff/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        String staffId = request.get("staffId");
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Staff staff = staffService.findByStaffId(staffId);
            
            // Verify current password
            boolean passwordMatches = false;
            if (staff.getPassword() != null) {
                if (passwordEncoder != null) {
                    passwordMatches = passwordEncoder.matches(currentPassword, staff.getPassword());
                } else {
                    // Fallback to simple comparison if encoder not available (for testing only)
                    passwordMatches = currentPassword.equals(staff.getPassword());
                }
            }
            
            // Check for default password
            if (!passwordMatches && "OMstaff123".equals(currentPassword)) {
                passwordMatches = true;
            }
            
            if (!passwordMatches) {
                response.put("success", false);
                response.put("message", "Current password is incorrect");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Update password
            staffService.updateStaffPassword(staffId, newPassword);
            
            response.put("success", true);
            response.put("message", "Password changed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to change password: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}