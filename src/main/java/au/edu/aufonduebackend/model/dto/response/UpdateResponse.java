package au.edu.aufonduebackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

<<<<<<< Updated upstream
=======
import java.time.Instant;
>>>>>>> Stashed changes
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UpdateResponse {
    private Long id;
    private Long issueId;
    private String status;
    private String comment;
    private Instant updateTime;
    private List<String> photoUrls;

}
