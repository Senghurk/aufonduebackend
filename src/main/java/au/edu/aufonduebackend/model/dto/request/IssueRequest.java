package au.edu.aufonduebackend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

// data for creating/updating an issue (description, location, photos)
@Getter
@Setter
public class IssueRequest {
    @NotBlank(message = "Description is required")
    private String description;

    private Double latitude;
    private Double longitude;
    private String customLocation;
    private String category;
    private String customCategory;
    private List<String> photoUrls;

    @JsonProperty("isUsingCustomLocation")
    private boolean usingCustomLocation;

    @NotBlank(message = "User email is required")
    private String userEmail;
}