package au.edu.aufonduebackend.service;


import au.edu.aufonduebackend.model.dto.response.AdminResponse;
import au.edu.aufonduebackend.model.dto.response.StaffResponse;
import au.edu.aufonduebackend.model.entity.Admin;

import java.util.List;

public interface AdminService {
    List<AdminResponse> getAllAdmins();
    AdminResponse addAdmin(Admin admin);
    boolean isAdminEmailAllowed(String email);
}