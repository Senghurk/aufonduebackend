package au.edu.aufonduebackend.controller;

import au.edu.aufonduebackend.model.dto.request.PasswordResetRequest;
import au.edu.aufonduebackend.model.dto.request.StaffCreateRequest;
import au.edu.aufonduebackend.model.dto.response.StaffResponse;
import au.edu.aufonduebackend.service.StaffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Endpoints for staff account management

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private static final Logger logger = LoggerFactory.getLogger(StaffController.class);
    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    // Endpoint to get all staff members
//    @GetMapping
//    public ResponseEntity<List<StaffResponse>> getAllStaff(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        List<StaffResponse> staffList = staffService.getAllStaff(page, size, null);
//        return ResponseEntity.ok(staffList);
//    }
    @GetMapping
    public List<StaffResponse> getAllStaff() {
        return staffService.getAllStaffWithoutPagination();
    }




    // Endpoint to get a specific staff member by ID
    @GetMapping("/{id}")
    public ResponseEntity<StaffResponse> getStaffById(@PathVariable Long id) {
        StaffResponse staff = staffService.getStaffById(id);
        return ResponseEntity.ok(staff);
    }

    // Endpoint to add a new staff member
//    @PostMapping
//    public ResponseEntity<StaffResponse> addStaff(@RequestBody Staff staff) {
//        Staff newStaff = ((StaffServiceImpl) staffService).addStaff(staff);
//        StaffResponse response = new StaffResponse();
//        response.setId(newStaff.getId());
//        response.setName(newStaff.getName());
//        response.setEmail(newStaff.getEmail());
//        return ResponseEntity.ok(response);
//    }

    // Endpoint to check if staff can be deleted
    @GetMapping("/{id}/can-delete")
    public ResponseEntity<Map<String, Object>> canDeleteStaff(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("canDelete", staffService.canDeleteStaff(id));
            response.put("incompleteReports", staffService.getIncompleteReportsCount(id));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("canDelete", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Endpoint to delete a staff member
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Long id) {
        try {
            staffService.deleteStaff(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Staff deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to delete staff with id {}: {}", id, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }



    // Endpoint to create a new staff member
    @PostMapping
    public ResponseEntity<?> createStaff(@RequestBody StaffCreateRequest request) {
        try {
            StaffResponse newStaff = staffService.createStaff(request);
            return new ResponseEntity<>(newStaff, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            
            // Check if error is about duplicate OM ID
            if (e.getMessage() != null && e.getMessage().contains("Staff ID already exists")) {
                errorResponse.put("message", "This OM ID (" + request.getStaffId() + ") is already assigned to another staff member. Please use a different OM ID.");
                errorResponse.put("errorType", "DUPLICATE_OM_ID");
            } else if (e.getMessage() != null && e.getMessage().contains("Email already exists")) {
                errorResponse.put("message", "This email address is already registered. Please use a different email.");
                errorResponse.put("errorType", "DUPLICATE_EMAIL");
            } else {
                errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "Failed to create staff member");
                errorResponse.put("errorType", "GENERAL_ERROR");
            }
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    // Endpoint to reset staff password
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetStaffPassword(@RequestBody PasswordResetRequest request) {
        try {
            // First ensure the staff member exists in Firebase
            staffService.ensureStaffInFirebase(request.getStaffId());
            
            String resetLink = staffService.resetStaffPassword(request.getStaffId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password reset link has been sent to staff email");
            response.put("email", request.getStaffEmail());
            response.put("resetLink", resetLink);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    // Endpoint to update password after reset
    @PostMapping("/update-password")
    public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody Map<String, String> request) {
        try {
            String staffId = request.get("staffId");
            String newPassword = request.get("newPassword");
            
            if (staffId == null || newPassword == null) {
                throw new RuntimeException("Staff ID and new password are required");
            }
            
            StaffResponse updatedStaff = staffService.updateStaffPassword(staffId, newPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password updated successfully");
            response.put("staff", updatedStaff);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    // Endpoint to update password from Firebase (after Firebase reset)
    @PostMapping("/update-password-firebase")
    public ResponseEntity<Map<String, Object>> updatePasswordFromFirebase(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String newPassword = request.get("newPassword");
            
            if (email == null || newPassword == null) {
                throw new RuntimeException("Email and new password are required");
            }
            
            // Find staff by email and update password
            StaffResponse updatedStaff = staffService.updateStaffPasswordByEmail(email, newPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password synced with backend successfully");
            response.put("staff", updatedStaff);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    // Endpoint to add mock staff data
    @PostMapping("/mock")
    public ResponseEntity<String> addMockStaff() {
        staffService.addMockData();
        return ResponseEntity.ok("Mock staff data added successfully!");
    }
    
    // Endpoint to sync all staff with Firebase
    @PostMapping("/sync-firebase")
    public ResponseEntity<Map<String, Object>> syncStaffWithFirebase() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<StaffResponse> allStaff = staffService.getAllStaffWithoutPagination();
            int synced = 0;
            int failed = 0;
            
            for (StaffResponse staffResponse : allStaff) {
                try {
                    staffService.ensureStaffInFirebase(staffResponse.getId());
                    synced++;
                } catch (Exception e) {
                    failed++;
                    logger.error("Failed to sync staff {} with Firebase: {}", staffResponse.getEmail(), e.getMessage());
                }
            }
            
            response.put("success", true);
            response.put("message", String.format("Sync completed. Synced: %d, Failed: %d", synced, failed));
            response.put("synced", synced);
            response.put("failed", failed);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to sync staff with Firebase: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}


