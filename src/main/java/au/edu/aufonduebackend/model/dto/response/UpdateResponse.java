package au.edu.aufonduebackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
    private LocalDateTime updateTime;
    private List<String> photoUrls;

}
