package au.edu.aufonduebackend.config;

import au.edu.aufonduebackend.model.entity.Admin;
import au.edu.aufonduebackend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Order(2) // Run after DataInitializer
public class AdminDataFixer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminDataFixer.class);
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Override
    public void run(String... args) throws Exception {
        fixAdminCreatedAtDates();
    }
    
    private void fixAdminCreatedAtDates() {
        try {
            List<Admin> admins = adminRepository.findAll();
            int updatedCount = 0;
            
            for (Admin admin : admins) {
                if (admin.getCreatedAt() == null) {
                    // Set a default created date for existing records
                    admin.setCreatedAt(LocalDateTime.now().minusDays(30)); // Set to 30 days ago as default
                    adminRepository.save(admin);
                    updatedCount++;
                    logger.info("Fixed createdAt date for admin: {} ({})", admin.getUsername(), admin.getEmail());
                }
            }
            
            if (updatedCount > 0) {
                logger.info("Fixed createdAt dates for {} admin records", updatedCount);
            }
        } catch (Exception e) {
            logger.error("Error fixing admin createdAt dates: {}", e.getMessage());
        }
    }
}