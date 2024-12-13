// File: src/main/java/au/edu/aufonduebackend/model/dto/response/IssueResponse.java

package au.edu.aufonduebackend.model.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class IssueResponse {
    private Long id;
    private String title;
    private String description;
    private Double latitude;
    private Double longitude;
    private String location;
    private String category;
    private String priority;
    private String status;
    private List<String> photoUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse reportedBy; // Changed from String to UserResponse
}