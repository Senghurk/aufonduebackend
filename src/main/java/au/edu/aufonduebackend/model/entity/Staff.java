package au.edu.aufonduebackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Represents maintenance staff who handle reported issues
@Entity
@Table(name = "staff")
@Getter
@Setter
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    
    @Column(name = "staff_id")
    private String staffId;
    
    private String password;
    
    private String role;
    
    @Column(name = "firebase_uid")
    private String firebaseUid;
    
    @Column(name = "first_login")
    private Boolean firstLogin = true;
    
    @Column(name = "password_reset_requested_at")
    private LocalDateTime passwordResetRequestedAt;
    
    @Column(name = "password_reset_completed_at")
    private LocalDateTime passwordResetCompletedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "assignedTo")
    private List<Issue> assignedIssues = new ArrayList<>();
}
