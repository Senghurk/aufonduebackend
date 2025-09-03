package au.edu.aufonduebackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "https://au-fondue-web.vercel.app"})
public class AuthController {

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String accessToken = request.get("accessToken");
        
        // Bypass authentication - always return success for admin login
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Admin login successful");
        
        Map<String, Object> data = new HashMap<>();
        data.put("userType", "admin");
        data.put("userId", 1L);
        data.put("name", "Admin User");
        data.put("email", email != null ? email : "admin@au.edu");
        data.put("firstLogin", false);
        data.put("token", "bypass-admin-token");
        
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/staff/login")
    public ResponseEntity<?> staffLogin(@RequestBody Map<String, String> request) {
        String omId = request.get("omId");
        String password = request.get("password");
        
        // Bypass authentication - always return success for staff login
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Staff login successful");
        
        Map<String, Object> data = new HashMap<>();
        data.put("userType", "staff");
        data.put("userId", 2L);
        data.put("name", "Staff User");
        data.put("email", "staff@au.edu");
        data.put("firstLogin", false);
        data.put("token", "bypass-staff-token");
        
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }
}