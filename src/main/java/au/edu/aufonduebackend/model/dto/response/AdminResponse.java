package au.edu.aufonduebackend.model.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdminResponse {
    private Long id;
    private String email;
    private String username;
    private LocalDateTime createdAt;
}
