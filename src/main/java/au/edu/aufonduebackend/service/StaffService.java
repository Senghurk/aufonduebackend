package au.edu.aufonduebackend.service;


import au.edu.aufonduebackend.model.dto.request.StaffCreateRequest;
import au.edu.aufonduebackend.model.dto.response.StaffResponse;
import au.edu.aufonduebackend.model.entity.Staff;

import java.util.List;

public interface StaffService {
    List<StaffResponse> getAllStaff(int page, int size, String status);  // Get all staff members
    StaffResponse getStaffById(Long id);  // Get staff by ID
    void deleteStaff(Long id);  // Delete a staff member
    void addMockData();
    List<StaffResponse> getAllStaffWithoutPagination();
    StaffResponse createStaff(StaffCreateRequest request);  // Create new staff
    String resetStaffPassword(Long staffId);  // Reset staff password
    StaffResponse updateStaffPassword(String staffId, String newPassword);  // Update password after reset
    StaffResponse updateStaffPasswordByEmail(String email, String newPassword);  // Update password by email (for Firebase sync)
    Staff findByStaffId(String staffId);  // Find staff by staff ID
    void ensureStaffInFirebase(Long staffId);  // Ensure staff exists in Firebase
}