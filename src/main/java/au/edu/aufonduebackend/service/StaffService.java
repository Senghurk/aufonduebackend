package au.edu.aufonduebackend.service;


import au.edu.aufonduebackend.model.dto.response.StaffResponse;
import au.edu.aufonduebackend.model.entity.Staff;

import java.util.List;

public interface StaffService {
    List<StaffResponse> getAllStaff(int page, int size, String status);  // Get all staff members
    StaffResponse getStaffById(Long id);  // Get staff by ID
    void deleteStaff(Long id);  // Delete a staff member
    void addMockData();
    List<StaffResponse> getAllStaffWithoutPagination();
}