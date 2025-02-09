package au.edu.aufonduebackend.model.dto.request;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UpdateRequest {

    private Long issueId;
    private String status;  // Keep status as String
    private String comment;
    private LocalDateTime updateTime;


}
