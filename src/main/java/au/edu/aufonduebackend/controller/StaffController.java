package au.edu.aufonduebackend.controller;

import au.edu.aufonduebackend.model.dto.response.StaffResponse;
import au.edu.aufonduebackend.model.entity.Staff;
import au.edu.aufonduebackend.service.StaffService;
import au.edu.aufonduebackend.service.impl.StaffServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

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

    // Endpoint to delete a staff member
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.ok("Staff deleted successfully.");
    }



    // Endpoint to add mock staff data
    @PostMapping
    public ResponseEntity<String> addMockStaff() {
        staffService.addMockData();
        return ResponseEntity.ok("Mock staff data added successfully!");
    }
}


