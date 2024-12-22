package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.model.entity.User;
import au.edu.aufonduebackend.repository.UserRepository;
import au.edu.aufonduebackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUserAfterAuthentication(String username, String email) {
        if (!email.toLowerCase().endsWith("@au.edu")) {
            throw new IllegalArgumentException("Only AU email addresses are allowed");
        }

        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setEmail(email);
                    newUser.setRole("USER");
                    return userRepository.save(newUser);
                });
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }
}