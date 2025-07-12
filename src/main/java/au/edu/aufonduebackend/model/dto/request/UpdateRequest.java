package au.edu.aufonduebackend.model.dto.request;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UpdateRequest {

    private Long issueId;
    private String status;
    private String comment;
    private Instant updateTime;


}
