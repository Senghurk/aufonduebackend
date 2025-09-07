package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.model.dto.response.AdminResponse;
import au.edu.aufonduebackend.model.entity.Admin;
import au.edu.aufonduebackend.repository.AdminRepository;
import au.edu.aufonduebackend.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    private AdminResponse convertToResponse(Admin admin) {
        AdminResponse response = new AdminResponse();
        response.setId(admin.getId());
        response.setUsername(admin.getUsername());
        response.setEmail(admin.getEmail());
        
        // Handle null createdAt for existing records
        if (admin.getCreatedAt() != null) {
            response.setCreatedAt(admin.getCreatedAt());
        } else {
            // For existing records without createdAt, set it to a default value
            // This ensures the frontend doesn't show N/A
            response.setCreatedAt(java.time.LocalDateTime.now());
        }
        
        return response;
    }
    @Override
    public AdminResponse addAdmin(Admin admin) {
        // Save the admin entity
        Admin savedAdmin = adminRepository.save(admin);
        // Convert saved admin to response DTO and return
        return convertToResponse(savedAdmin);
    }

    @Override
    public List<AdminResponse> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        return admins.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }




    @Override
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

}
