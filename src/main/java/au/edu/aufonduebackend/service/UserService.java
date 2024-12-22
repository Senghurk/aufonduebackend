package au.edu.aufonduebackend.service;

import au.edu.aufonduebackend.model.entity.User;

public interface UserService {
    User createUserAfterAuthentication(String username, String email);
}