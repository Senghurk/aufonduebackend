package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.model.dto.request.StaffCreateRequest;
import au.edu.aufonduebackend.model.dto.response.StaffResponse;
import au.edu.aufonduebackend.model.entity.Staff;
import au.edu.aufonduebackend.repository.StaffRepository;
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
    private final FirebaseAuthService firebaseAuthService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public StaffServiceImpl(StaffRepository staffRepository, 
                           @Autowired(required = false) FirebaseAuthService firebaseAuthService,
                           @Autowired(required = false) BCryptPasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
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
    public void deleteStaff(Long id) {
        if (!staffRepository.existsById(id)) {
            throw new RuntimeException("Staff not found with id: " + id);
        }
        staffRepository.deleteById(id);
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
        
        // If Firebase UID is null or empty, create Firebase user
        if ((staff.getFirebaseUid() == null || staff.getFirebaseUid().isEmpty()) && firebaseAuthService != null) {
            try {
                logger.info("Creating Firebase user for staff: {} ({})", staff.getStaffId(), staff.getEmail());
                String firebaseUid = firebaseAuthService.createUserForStaff(staff.getEmail(), staff.getStaffId());
                staff.setFirebaseUid(firebaseUid);
                staffRepository.save(staff);
                logger.info("Firebase user created successfully for staff: {}", staff.getStaffId());
            } catch (Exception e) {
                logger.error("Failed to create Firebase user for staff: {}", staff.getStaffId(), e);
                throw new RuntimeException("Failed to create Firebase account for staff. Email may already be in use or invalid: " + e.getMessage());
            }
        } else if (firebaseAuthService == null) {
            logger.warn("Firebase service is not available");
            throw new RuntimeException("Password reset service is currently unavailable");
        }
    }
}
