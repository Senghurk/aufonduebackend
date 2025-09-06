package au.edu.aufonduebackend.config;

import au.edu.aufonduebackend.model.entity.Staff;
import au.edu.aufonduebackend.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private StaffRepository staffRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Checking for initial staff data...");
        
        // Only add test data if no staff exists
        if (staffRepository.count() == 0) {
            logger.info("No staff found. Adding test staff members...");
            
            // Create test staff 1
            if (!staffRepository.existsByStaffId("OM01")) {
                Staff staff1 = new Staff();
                staff1.setStaffId("OM01");
                staff1.setName("John Doe");
                staff1.setEmail("john.doe@gmail.com");
                staff1.setPassword(passwordEncoder.encode("OMstaff123"));
                staff1.setRole("STAFF");
                staff1.setFirstLogin(true);
                staff1.setCreatedAt(LocalDateTime.now());
                staff1.setUpdatedAt(LocalDateTime.now());
                staffRepository.save(staff1);
                logger.info("Created test staff: OM01");
            }
            
            // Create test staff 2
            if (!staffRepository.existsByStaffId("OM02")) {
                Staff staff2 = new Staff();
                staff2.setStaffId("OM02");
                staff2.setName("Jane Smith");
                staff2.setEmail("jane.smith@gmail.com");
                staff2.setPassword(passwordEncoder.encode("OMstaff123"));
                staff2.setRole("STAFF");
                staff2.setFirstLogin(true);
                staff2.setCreatedAt(LocalDateTime.now());
                staff2.setUpdatedAt(LocalDateTime.now());
                staffRepository.save(staff2);
                logger.info("Created test staff: OM02");
            }
            
            logger.info("Test staff initialization completed");
        } else {
            logger.info("Staff data already exists. Skipping initialization.");
        }
    }
}