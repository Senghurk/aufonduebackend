package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.model.dto.request.StaffCreateRequest;
import au.edu.aufonduebackend.model.dto.response.StaffResponse;
import au.edu.aufonduebackend.model.entity.Staff;
import au.edu.aufonduebackend.repository.StaffRepository;
import au.edu.aufonduebackend.repository.IssueRepository;
import au.edu.aufonduebackend.service.FirebaseAuthService;
import au.edu.aufonduebackend.service.StaffService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class StaffServiceImpl implements StaffService {

    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);
    private final StaffRepository staffRepository;
    private final IssueRepository issueRepository;
    private final FirebaseAuthService firebaseAuthService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public StaffServiceImpl(StaffRepository staffRepository,
                           IssueRepository issueRepository,
                           @Autowired(required = false) FirebaseAuthService firebaseAuthService,
                           @Autowired(required = false) BCryptPasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.issueRepository = issueRepository;
        this.firebaseAuthService = firebaseAuthService;
        this.passwordEncoder = passwordEncoder != null ? passwordEncoder : new BCryptPasswordEncoder();
    }

    @Override
    public List<StaffResponse> getAllStaff(int page, int size, String status) {
        // Fetch all staff members with pagination
        return staffRepository.findAll(PageRequest.of(page, size)).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StaffResponse getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));
        return convertToResponse(staff);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));
        
        // Check if staff has any incomplete assigned reports
        long incompleteReportsCount = issueRepository.countIncompleteAssignedIssues(id);
        if (incompleteReportsCount > 0) {
            String staffIdentifier = staff.getStaffId() != null ? staff.getStaffId() : staff.getName();
            throw new RuntimeException(
                "Cannot delete staff member '" + staffIdentifier + 
                "' because they have " + incompleteReportsCount + 
                " assigned report(s) that are not completed. " +
                "All assigned reports must be marked as 'Completed' before deletion."
            );
        }
        
        // Handle issue reassignment based on completion status
        try {
            // Unassign incomplete issues (they go back to unassigned pool)
            issueRepository.unassignIncompleteIssuesFromStaff(id);
            logger.info("Unassigned incomplete issues from staff: {}", 
                staff.getStaffId() != null ? staff.getStaffId() : "legacy-" + id);
            
            // Remove staff reference from completed issues (but keep them as completed)
            issueRepository.removeStaffFromCompletedIssues(id);
            logger.info("Removed staff reference from completed issues for: {}", 
                staff.getStaffId() != null ? staff.getStaffId() : "legacy-" + id);
        } catch (Exception e) {
            logger.warn("Failed to handle issue reassignment for staff {}: {}", 
                staff.getStaffId() != null ? staff.getStaffId() : "legacy-" + id, e.getMessage());
        }
        
        // Try to delete from Firebase if the user exists there
        if (staff.getFirebaseUid() != null && !staff.getFirebaseUid().isEmpty() && firebaseAuthService != null) {
            try {
                firebaseAuthService.deleteUser(staff.getFirebaseUid());
                logger.info("Deleted Firebase user for staff: {}", 
                    staff.getStaffId() != null ? staff.getStaffId() : "legacy-" + id);
            } catch (Exception e) {
                logger.warn("Failed to delete Firebase user for staff {}: {}", 
                    staff.getStaffId() != null ? staff.getStaffId() : "legacy-" + id, e.getMessage());
                // Continue with deletion even if Firebase deletion fails
            }
        } else {
            logger.info("No Firebase account to delete for staff: {}", 
                staff.getStaffId() != null ? staff.getStaffId() : "legacy-" + id);
        }
        
        // Delete from database
        staffRepository.deleteById(id);
        logger.info("Staff deleted successfully: {}", 
            staff.getStaffId() != null ? staff.getStaffId() : "legacy-" + id);
    }

    public Staff addStaff(Staff staff) {
        // Save the new staff to the database
        return staffRepository.save(staff);
    }

    // Helper method to convert Staff to StaffResponse
    private StaffResponse convertToResponse(Staff staff) {
        StaffResponse response = new StaffResponse();
        response.setId(staff.getId());
        response.setStaffId(staff.getStaffId());
        response.setName(staff.getName());
        response.setEmail(staff.getEmail());
        response.setRole(staff.getRole());
        response.setFirstLogin(staff.getFirstLogin());
        response.setCreatedAt(staff.getCreatedAt());
        response.setUpdatedAt(staff.getUpdatedAt());
        response.setPasswordResetRequestedAt(staff.getPasswordResetRequestedAt());
        return response;
    }

    @Override
    public void addMockData() {
        List<Staff> mockStaff = List.of(
                createMockStaff("John Doe", "JohnDoe@example.com"),
                createMockStaff("Jane Smith", "JaneSmith@example.com"),
                createMockStaff("Michael Brown", "michael.brown@example.com")
        );

        staffRepository.saveAll(mockStaff);
    }

    private Staff createMockStaff(String name, String email) {
        Staff staff = new Staff();
        staff.setName(name);
        staff.setEmail(email);

        return staff;
    }

    @Override
    public List<StaffResponse> getAllStaffWithoutPagination() {
        List<Staff> staffList = staffRepository.findAll();  // Without pagination for testing
        return staffList.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public StaffResponse createStaff(StaffCreateRequest request) {
        // Check if staff ID already exists
        if (staffRepository.existsByStaffId(request.getStaffId())) {
            throw new RuntimeException("Staff ID already exists: " + request.getStaffId());
        }
        
        // Check if email already exists
        if (staffRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        Staff staff = new Staff();
        staff.setStaffId(request.getStaffId());
        staff.setName(request.getName());
        staff.setEmail(request.getEmail());
        staff.setPassword(passwordEncoder.encode("OMstaff123"));
        staff.setRole("STAFF");
        staff.setFirstLogin(true);
        
        // Parse date string to LocalDateTime
        LocalDateTime createdAt = LocalDateTime.now();
        if (request.getDateAdded() != null && !request.getDateAdded().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(request.getDateAdded());
                createdAt = date.atStartOfDay();
            } catch (Exception e) {
                logger.warn("Failed to parse date: {}, using current time", request.getDateAdded());
            }
        }
        staff.setCreatedAt(createdAt);
        staff.setUpdatedAt(LocalDateTime.now());
        
        // Try to create Firebase user if service is available
        if (firebaseAuthService != null) {
            try {
                String firebaseUid = firebaseAuthService.createUserForStaff(request.getEmail(), request.getStaffId());
                staff.setFirebaseUid(firebaseUid);
            } catch (Exception e) {
                logger.error("Failed to create Firebase user for staff: {}", request.getStaffId(), e);
                // Continue without Firebase if it fails
            }
        } else {
            logger.info("Firebase service not available, skipping Firebase user creation");
        }
        
        Staff savedStaff = staffRepository.save(staff);
        return convertToResponse(savedStaff);
    }
    
    @Override
    public String resetStaffPassword(Long staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + staffId));
        
        if (staff.getEmail() == null || staff.getEmail().isEmpty()) {
            throw new RuntimeException("Staff member has no email address");
        }
        
        // Just track that a reset was requested
        // The actual email sending is handled by the frontend using Firebase Client SDK
        staff.setPasswordResetRequestedAt(LocalDateTime.now());
        staff.setFirstLogin(false); // Don't force first login since they're resetting
        staffRepository.save(staff);
        
        logger.info("Password reset tracked for staff: {} ({})", staff.getStaffId(), staff.getEmail());
        
        // Return success message
        return "Password reset request tracked. Email will be sent via Firebase.";
    }
    
    @Override
    public StaffResponse updateStaffPassword(String staffId, String newPassword) {
        Staff staff = staffRepository.findByStaffId(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with staff ID: " + staffId));
        
        // Update password in database
        staff.setPassword(passwordEncoder.encode(newPassword));
        staff.setFirstLogin(false);
        staff.setPasswordResetCompletedAt(LocalDateTime.now());
        staff.setUpdatedAt(LocalDateTime.now());
        
        // Update password in Firebase if user exists and service is available
        if (staff.getFirebaseUid() != null && firebaseAuthService != null) {
            try {
                firebaseAuthService.updatePassword(staff.getFirebaseUid(), newPassword);
            } catch (Exception e) {
                logger.error("Failed to update Firebase password for staff: {}", staffId, e);
                // Continue even if Firebase update fails
            }
        }
        
        Staff updatedStaff = staffRepository.save(staff);
        return convertToResponse(updatedStaff);
    }
    
    @Override
    public StaffResponse updateStaffPasswordByEmail(String email, String newPassword) {
        Staff staff = staffRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Staff not found with email: " + email));
        
        // Update password in database
        staff.setPassword(passwordEncoder.encode(newPassword));
        staff.setFirstLogin(false);
        staff.setPasswordResetCompletedAt(LocalDateTime.now());
        staff.setUpdatedAt(LocalDateTime.now());
        
        // Update password in Firebase if user exists and service is available
        if (staff.getFirebaseUid() != null && firebaseAuthService != null) {
            try {
                firebaseAuthService.updatePassword(staff.getFirebaseUid(), newPassword);
            } catch (Exception e) {
                logger.error("Failed to update Firebase password for staff: {}", staff.getStaffId(), e);
                // Continue even if Firebase update fails
            }
        }
        
        Staff updatedStaff = staffRepository.save(staff);
        return convertToResponse(updatedStaff);
    }
    
    @Override
    public Staff findByStaffId(String staffId) {
        return staffRepository.findByStaffId(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with staff ID: " + staffId));
    }
    
    @Override
    public void ensureStaffInFirebase(Long staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + staffId));
        
        // Check if email is valid
        if (staff.getEmail() == null || staff.getEmail().isEmpty()) {
            throw new RuntimeException("Staff member has no email address configured");
        }
        
        // If Firebase service is not available
        if (firebaseAuthService == null) {
            logger.warn("Firebase service is not available");
            throw new RuntimeException("Password reset service is currently unavailable. Please contact system administrator.");
        }
        
        // If Firebase UID is null or empty, create Firebase user
        if (staff.getFirebaseUid() == null || staff.getFirebaseUid().isEmpty()) {
            try {
                logger.info("Creating Firebase user for staff: {} ({})", staff.getStaffId(), staff.getEmail());
                String firebaseUid = firebaseAuthService.createUserForStaff(staff.getEmail(), staff.getStaffId());
                staff.setFirebaseUid(firebaseUid);
                staffRepository.save(staff);
                logger.info("Firebase user created successfully for staff: {}", staff.getStaffId());
            } catch (Exception e) {
                logger.error("Failed to create Firebase user for staff: {} - {}", staff.getStaffId(), e.getMessage());
                
                // If user already exists, try to get the existing user and update our record
                if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                    try {
                        com.google.firebase.auth.UserRecord existingUser = firebaseAuthService.getUserByEmail(staff.getEmail());
                        staff.setFirebaseUid(existingUser.getUid());
                        staffRepository.save(staff);
                        logger.info("Found existing Firebase user for staff: {}, updated record", staff.getStaffId());
                        return; // Success - user exists
                    } catch (Exception e2) {
                        logger.error("Failed to retrieve existing Firebase user: {}", e2.getMessage());
                    }
                }
                
                // Provide more specific error messages
                String errorMsg = "Failed to setup Firebase authentication for " + staff.getEmail() + ". ";
                if (e.getMessage() != null && e.getMessage().contains("invalid")) {
                    errorMsg += "The email address may be invalid.";
                } else if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                    errorMsg += "The email may be associated with another account.";
                } else {
                    errorMsg += "Please contact system administrator.";
                }
                throw new RuntimeException(errorMsg);
            }
        } else {
            logger.info("Staff {} already has Firebase UID: {}", staff.getStaffId(), staff.getFirebaseUid());
        }
    }
    
    @Override
    public boolean canDeleteStaff(Long staffId) {
        if (!staffRepository.existsById(staffId)) {
            throw new RuntimeException("Staff not found with id: " + staffId);
        }
        return issueRepository.countIncompleteAssignedIssues(staffId) == 0;
    }
    
    @Override
    public long getIncompleteReportsCount(Long staffId) {
        if (!staffRepository.existsById(staffId)) {
            throw new RuntimeException("Staff not found with id: " + staffId);
        }
        return issueRepository.countIncompleteAssignedIssues(staffId);
    }
    
    @Override
    public StaffResponse updateStaffName(Long id, String newName) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));
        
        // Validate new name
        if (newName == null || newName.trim().isEmpty()) {
            throw new RuntimeException("Staff name cannot be empty");
        }
        
        // Update the name
        staff.setName(newName.trim());
        staff.setUpdatedAt(LocalDateTime.now());
        
        Staff updatedStaff = staffRepository.save(staff);
        return convertToResponse(updatedStaff);
    }
}
