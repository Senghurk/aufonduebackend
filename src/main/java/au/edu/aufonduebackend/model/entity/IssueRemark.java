package au.edu.aufonduebackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "issue_remarks")
@Getter
@Setter
public class IssueRemark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false, unique = true)
    private Issue issue;

    @Column(name = "remark_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RemarkType remarkType;

    @Column(name = "is_viewed", nullable = false)
    private Boolean isViewed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private Admin createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum RemarkType {
        NEW("new"),
        OK("OK"),
        RF("RF"),
        PR("PR");

        private final String value;

        RemarkType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static RemarkType fromString(String text) {
            for (RemarkType type : RemarkType.values()) {
                if (type.value.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("No constant with text " + text + " found");
        }
    }
}