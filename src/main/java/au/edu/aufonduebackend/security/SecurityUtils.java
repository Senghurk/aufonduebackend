package au.edu.aufonduebackend.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class SecurityUtils {
    
    /**
     * Check if the current request is from an admin user
     * This is a simplified check - in production, you should use proper JWT/OAuth validation
     */
    public static boolean isAdminRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            
            // Check for admin token or header
            String authHeader = request.getHeader("Authorization");
            String userType = request.getHeader("X-User-Type");
            
            // Check if user type header indicates admin
            if ("admin".equalsIgnoreCase(userType)) {
                return true;
            }
            
            // Check if token contains admin identifier
            if (authHeader != null && authHeader.contains("admin")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if the current request is from a staff user
     */
    public static boolean isStaffRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            
            String userType = request.getHeader("X-User-Type");
            
            // Check if user type header indicates staff
            if ("staff".equalsIgnoreCase(userType) || "om_staff".equalsIgnoreCase(userType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get the current user ID from the request
     */
    public static Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            
            String userId = request.getHeader("X-User-Id");
            if (userId != null) {
                try {
                    return Long.parseLong(userId);
                } catch (NumberFormatException e) {
                    // Invalid user ID
                }
            }
        }
        return null;
    }
}