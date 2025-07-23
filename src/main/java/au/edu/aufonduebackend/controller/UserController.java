package au.edu.aufonduebackend.controller;

import au.edu.aufonduebackend.model.dto.response.ApiResponse;
import au.edu.aufonduebackend.model.dto.response.UserResponse;
import au.edu.aufonduebackend.model.entity.User;
import au.edu.aufonduebackend.service.UserService;
import au.edu.aufonduebackend.service.FcmService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Endpoints for user account management with Microsoft authentication

@RestController
@RequestMapping("/api/users")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FcmService fcmService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // If Authorization header is present, verify it's Microsoft + @au.edu
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String idToken = authHeader.substring(7);

                try {
                    FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                    String tokenEmail = decodedToken.getEmail();

                    // Validate AU domain
                    if (tokenEmail == null || !tokenEmail.endsWith("@au.edu")) {
                        return ResponseEntity.badRequest()
                                .body(ApiResponse.error("Access restricted to AU university accounts only"));
                    }

                    // Use email from token instead of parameter
                    email = tokenEmail;
                    username = decodedToken.getName() != null ?
                            decodedToken.getName() : email.substring(0, email.indexOf("@"));

                } catch (Exception e) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("Invalid authentication token"));
                }
            }

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

    @PostMapping("/update-fcm-token")
    public ResponseEntity<ApiResponse<String>> updateFcmToken(
            @RequestParam String email,
            @RequestParam String fcmToken,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        try {
            // If Authorization header is present, get email from token
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String idToken = authHeader.substring(7);

                try {
                    FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                    String tokenEmail = decodedToken.getEmail();

                    if (tokenEmail != null && tokenEmail.endsWith("@au.edu")) {
                        email = tokenEmail; // Use email from token
                    }
                } catch (Exception e) {
                    // If token verification fails, fall back to parameter
                }
            }

            userService.updateFcmToken(email, fcmToken);
            return ResponseEntity.ok(new ApiResponse<>(true, "FCM token updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Failed to update FCM token: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/remove-fcm-token")
    public ResponseEntity<ApiResponse<String>> removeFcmToken(
            @RequestParam String email,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // If Authorization header is present, get email from token
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String idToken = authHeader.substring(7);

                try {
                    FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                    String tokenEmail = decodedToken.getEmail();

                    if (tokenEmail != null && tokenEmail.endsWith("@au.edu")) {
                        email = tokenEmail; // Use email from token
                    }
                } catch (Exception e) {
                    // If token verification fails, fall back to parameter
                }
            }

            userService.removeFcmToken(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "FCM token removed successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Failed to remove FCM token: " + e.getMessage(), null));
        }
    }

    @PostMapping("/test-notification")
    public ResponseEntity<ApiResponse<String>> sendTestNotification(
            @RequestBody TestNotificationRequest request) {

        try {
            // Find user by email
            User user = userService.findByEmail(request.getEmail());

            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "User not found", null));
            }

            if (user.getFcmToken() == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "User doesn't have FCM token registered", null));
            }

            // Send test notification
            boolean sent = fcmService.sendTestNotification(
                    user.getFcmToken(),
                    "Test Notification",
                    request.getMessage()
            );

            if (sent) {
                return ResponseEntity.ok(
                        new ApiResponse<>(true, "Test notification sent successfully", null));
            } else {
                return ResponseEntity.status(500)
                        .body(new ApiResponse<>(false, "Failed to send notification", null));
            }

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(false, "Failed to send notification: " + e.getMessage(), null));
        }
    }

    // DTO for test notification request
    public static class TestNotificationRequest {
        private String email;
        private String message;

        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}