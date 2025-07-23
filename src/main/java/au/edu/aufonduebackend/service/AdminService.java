package au.edu.aufonduebackend.service;


import au.edu.aufonduebackend.model.dto.response.AdminResponse;
import au.edu.aufonduebackend.model.dto.response.StaffResponse;
import au.edu.aufonduebackend.model.entity.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    List<AdminResponse> getAllAdmins();
    AdminResponse addAdmin(Admin admin);
    String getAdminStatusByEmail(String email) ;

    Optional<Admin> findByEmail(String email);
}