package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.model.dto.response.StaffResponse;
import au.edu.aufonduebackend.model.entity.Staff;
import au.edu.aufonduebackend.repository.StaffRepository;
import au.edu.aufonduebackend.service.StaffService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;

    public StaffServiceImpl(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
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
        response.setName(staff.getName());
        response.setEmail(staff.getEmail());
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
}
