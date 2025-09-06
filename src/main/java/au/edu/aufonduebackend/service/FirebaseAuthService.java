package au.edu.aufonduebackend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = false)
public class FirebaseAuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthService.class);
    private final FirebaseAuth firebaseAuth;
    
    public FirebaseAuthService() {
        try {
            this.firebaseAuth = FirebaseAuth.getInstance();
            logger.info("Firebase Auth Service initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Firebase Auth Service", e);
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }
    
    public String createUserForStaff(String email, String staffId) throws FirebaseAuthException {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword("OMstaff123")
                    .setEmailVerified(false)
                    .setDisplayName(staffId);
            
            UserRecord userRecord = firebaseAuth.createUser(request);
            logger.info("Successfully created Firebase user for staff: {}", staffId);
            return userRecord.getUid();
        } catch (FirebaseAuthException e) {
            if (e.getMessage().contains("already exists")) {
                UserRecord existingUser = firebaseAuth.getUserByEmail(email);
                logger.info("Firebase user already exists for email: {}", email);
                return existingUser.getUid();
            }
            throw e;
        }
    }
    
    public String generatePasswordResetLink(String email) throws FirebaseAuthException {
        try {
            // Generate password reset link with action code settings
            // This will trigger Firebase to send the email using the configured template
            String link = firebaseAuth.generatePasswordResetLink(email);
            logger.info("Password reset email triggered for: {}", email);
            logger.info("Reset link generated: {}", link);
            
            // Note: Firebase will automatically send the email using the template
            // configured in Firebase Console under Authentication > Templates
            return link;
        } catch (FirebaseAuthException e) {
            logger.error("Failed to generate password reset link for: {}", email, e);
            throw e;
        }
    }
    
    public void updatePassword(String uid, String newPassword) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                .setPassword(newPassword);
        
        firebaseAuth.updateUser(request);
        logger.info("Password updated for user: {}", uid);
    }
    
    public UserRecord getUserByEmail(String email) throws FirebaseAuthException {
        return firebaseAuth.getUserByEmail(email);
    }
    
    public void deleteUser(String uid) throws FirebaseAuthException {
        firebaseAuth.deleteUser(uid);
        logger.info("Firebase user deleted: {}", uid);
    }
}