package au.edu.aufonduebackend.model.dto.response;

import lombok.Data;

// user information for profile displays
@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
}