package au.edu.aufonduebackend.service;

import com.google.firebase.FirebaseApp;
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
    private FirebaseAuth firebaseAuth;
    private boolean isInitialized = false;
    
    public FirebaseAuthService() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                this.firebaseAuth = FirebaseAuth.getInstance();
                this.isInitialized = true;
                logger.info("Firebase Auth Service initialized successfully");
            } else {
                logger.warn("Firebase App not initialized - Firebase Auth Service will be disabled");
                this.isInitialized = false;
            }
        } catch (Exception e) {
            logger.error("Failed to initialize Firebase Auth Service - service will be disabled", e);
            this.isInitialized = false;
        }
    }
    
    public String createUserForStaff(String email, String staffId) throws FirebaseAuthException {
        if (!isInitialized) {
            logger.warn("Firebase Auth Service not initialized - cannot create user");
            return null;
        }
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
        if (!isInitialized) {
            logger.warn("Firebase Auth Service not initialized - cannot generate password reset link");
            return null;
        }
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
        if (!isInitialized) {
            logger.warn("Firebase Auth Service not initialized - cannot update password");
            return;
        }
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                .setPassword(newPassword);
        
        firebaseAuth.updateUser(request);
        logger.info("Password updated for user: {}", uid);
    }
    
    public UserRecord getUserByEmail(String email) throws FirebaseAuthException {
        if (!isInitialized) {
            logger.warn("Firebase Auth Service not initialized - cannot get user by email");
            return null;
        }
        return firebaseAuth.getUserByEmail(email);
    }
    
    public void deleteUser(String uid) throws FirebaseAuthException {
        if (!isInitialized) {
            logger.warn("Firebase Auth Service not initialized - cannot delete user");
            return;
        }
        firebaseAuth.deleteUser(uid);
        logger.info("Firebase user deleted: {}", uid);
    }
}