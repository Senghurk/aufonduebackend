package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.model.dto.response.AdminResponse;
import au.edu.aufonduebackend.model.entity.Admin;
import au.edu.aufonduebackend.repository.AdminRepository;
import au.edu.aufonduebackend.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public boolean isAdminEmailAllowed(String email) {
        return adminRepository.existsByEmail(email);
    }
}
