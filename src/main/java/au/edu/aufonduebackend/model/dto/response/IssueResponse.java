package au.edu.aufonduebackend.model.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

// issue data for client
@Getter
@Setter
public class IssueResponse {
    private Long id;
    private String description;
    private Double latitude;
    private Double longitude;
    private String customLocation;
    private Boolean usingCustomLocation;
    private String category;
    private String status;
    private List<String> photoUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse reportedBy;
    private boolean assigned;
    private StaffResponse assignedTo;
}