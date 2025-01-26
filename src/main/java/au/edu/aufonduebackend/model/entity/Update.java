package au.edu.aufonduebackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    private String status;

    @Column(nullable = true)
    private String comment;

    @ElementCollection
    @CollectionTable(name = "update_photos", joinColumns = @JoinColumn(name = "update_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();


}

