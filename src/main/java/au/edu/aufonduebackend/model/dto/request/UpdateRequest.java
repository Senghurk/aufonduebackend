package au.edu.aufonduebackend.model.dto.request;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// data for creating updates to issues (status, comments)
@Getter
@Setter
public class UpdateRequest {

    private Long issueId;
    private String status;
    private String comment;
    private Instant updateTime;
    private String updatedBy;


}
