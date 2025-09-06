package au.edu.aufonduebackend.model.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

// staff member data for assignment interfaces
@Getter
@Setter
public class StaffResponse {
    private Long id;
    private String staffId;
    private String name;
    private String email;
    private String role;
    private Boolean firstLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime passwordResetRequestedAt;
}
