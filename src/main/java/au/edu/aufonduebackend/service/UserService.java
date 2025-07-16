package au.edu.aufonduebackend.service;

import au.edu.aufonduebackend.model.entity.User;

public interface UserService {
    User createUserAfterAuthentication(String username, String email);

    // ADD THESE NEW METHODS FOR FCM
    User updateFcmToken(String email, String fcmToken);
    User removeFcmToken(String email);
    User findByEmail(String email);
}