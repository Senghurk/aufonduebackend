package au.edu.aufonduebackend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
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

    @Column(nullable = false)
    private String role;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Optional: Add a constructor that takes required fields
    public User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.role = "USER";  // Default role
    }
}