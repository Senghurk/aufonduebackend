package au.edu.aufonduebackend.model.dto.request;

import lombok.Data;

@Data
public class StaffCreateRequest {
    private String staffId;
    private String name;
    private String email;
    private String dateAdded; // Will be converted to LocalDateTime in service
}