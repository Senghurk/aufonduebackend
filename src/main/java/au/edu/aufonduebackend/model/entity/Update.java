package au.edu.aufonduebackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
public class Update {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @Column(nullable = false)
    private String status;  //  update the Issue's status

    @Column(nullable = true)
    private String comment;

    @ElementCollection
    @CollectionTable(name = "update_photos", joinColumns = @JoinColumn(name = "update_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();

    @CreationTimestamp
    private Instant updateTime;

    // Method to update the issue status when update status changes
    public void setStatus(String status) {
        this.status = status;
        if (issue != null) {
            issue.setStatus(status);
        }
    }
}

