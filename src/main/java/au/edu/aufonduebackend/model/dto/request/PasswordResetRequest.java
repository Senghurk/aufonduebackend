package au.edu.aufonduebackend.model.dto.request;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private Long staffId;
    private String staffEmail;
}