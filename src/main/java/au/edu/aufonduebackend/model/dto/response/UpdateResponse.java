package au.edu.aufonduebackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.Instant;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResponse {
    private Long id;
    private Long issueId;
    private String status;
    private String comment;
    private Instant updateTime;
    private List<String> photoUrls;

    // ADD THESE NEW FIELDS FOR NOTIFICATION STATUS
    private Boolean notificationSent;
    private String notificationError;

    // Constructor without notification fields for backward compatibility
    public UpdateResponse(Long id, Long issueId, String status, String comment,
                          Instant updateTime, List<String> photoUrls) {
        this.id = id;
        this.issueId = issueId;
        this.status = status;
        this.comment = comment;
        this.updateTime = updateTime;
        this.photoUrls = photoUrls;
        this.notificationSent = null;
        this.notificationError = null;
    }
}