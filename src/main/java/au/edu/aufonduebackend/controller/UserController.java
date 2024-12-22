package au.edu.aufonduebackend.controller;

import au.edu.aufonduebackend.model.dto.response.ApiResponse;
import au.edu.aufonduebackend.model.dto.response.UserResponse;
import au.edu.aufonduebackend.model.entity.User;
import au.edu.aufonduebackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestParam String username,
            @RequestParam String email) {
        try {
            User user = userService.createUserAfterAuthentication(username, email);
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());

            return ResponseEntity.ok(ApiResponse.success(response, "User created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error creating user"));
        }
    }
}