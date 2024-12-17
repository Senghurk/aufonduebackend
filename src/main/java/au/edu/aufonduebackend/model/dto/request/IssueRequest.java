// File: src/main/java/au/edu/aufonduebackend/model/dto/request/IssueRequest.java

package au.edu.aufonduebackend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class IssueRequest {
    @NotBlank(message = "Title is required")
    private String title;  // Added this field

    @NotBlank(message = "Description is required")
    private String description;

    // Location fields - either (latitude + longitude) OR customLocation must be provided
    private Double latitude;
    private Double longitude;
    private String customLocation;
    private boolean isUsingCustomLocation;

    private String category;
    private String customCategory;
    private List<String> photoUrls;
}