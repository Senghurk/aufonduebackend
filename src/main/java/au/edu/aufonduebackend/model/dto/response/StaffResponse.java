package au.edu.aufonduebackend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

// staff member data for assignment interfaces
@Getter
@Setter
public class StaffResponse {
    private Long id;
    private String name;
    private String email;
}
