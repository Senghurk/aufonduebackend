// File: src/main/java/au/edu/aufonduebackend/model/entity/User.java

package au.edu.aufonduebackend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
<<<<<<< Updated upstream
import org.hibernate.annotations.UpdateTimestamp;

=======

import java.time.Instant;
>>>>>>> Stashed changes
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;


    @Column(nullable = false, unique = true)
    private String email;

    @CreationTimestamp
<<<<<<< Updated upstream
    private LocalDateTime createdAt;
=======
    @Column(name = "created_at")
    private Instant createdAt;
>>>>>>> Stashed changes

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}