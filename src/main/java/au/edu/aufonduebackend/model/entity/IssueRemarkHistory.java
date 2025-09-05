package au.edu.aufonduebackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "issue_remarks_history")
@Getter
@Setter
public class IssueRemarkHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @Column(name = "remark_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private IssueRemark.RemarkType remarkType;

    @Column(name = "status_at_time", length = 50)
    private String statusAtTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id")
    private Admin changedBy;

    @CreationTimestamp
    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private Action action;

    public enum Action {
        CREATED("created"),
        UPDATED("updated"),
        VIEWED("viewed");

        private final String value;

        Action(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}