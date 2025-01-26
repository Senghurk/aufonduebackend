package au.edu.aufonduebackend.repository;

import au.edu.aufonduebackend.model.dto.response.IssueResponse;
import au.edu.aufonduebackend.model.entity.Issue;
import au.edu.aufonduebackend.model.entity.Staff;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByStatus(String status);

    List<Issue> findByReportedByIdOrderByCreatedAtDesc(Long userId);

    List<Issue> findAllByOrderByCreatedAtDesc();

    @Query(value = """
            SELECT * FROM issues i
            WHERE i.reported_by_user_id = :userId 
            AND (:status IS NULL OR i.status = :status)
            ORDER BY i.created_at DESC
            LIMIT :size OFFSET :offset
            """, nativeQuery = true)
    List<Issue> findUserIssues(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("size") int size
    );

    @Query(value = """
            SELECT * FROM issues i
            WHERE (:status IS NULL OR i.status = :status)
            ORDER BY i.created_at DESC
            LIMIT :size OFFSET :offset
            """, nativeQuery = true)
    List<Issue> findAllIssues(
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("size") int size
    );

    @Query(value = """
            SELECT * FROM issues i
            WHERE ST_DWithin(
                ST_MakePoint(i.longitude, i.latitude)::geography,
                ST_MakePoint(:longitude, :latitude)::geography,
                :radiusMeters
            )
            ORDER BY i.created_at DESC
            """, nativeQuery = true)
    List<Issue> findNearbyIssues(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusMeters") Double radiusMeters
    );


    // Admin Update
    @Modifying
    @Query("UPDATE Issue i SET i.assigned = true, i.assignedTo = :staff WHERE i.id = :issueId")
    void assignIssueToStaff(@Param("issueId") Long issueId, @Param("staff") Staff staff);




    @Query("SELECT i FROM Issue i WHERE i.assigned = false")
    List<Issue> findByAssignedFalse();

    @Query("SELECT i FROM Issue i WHERE i.assigned = true")
    List<Issue> findByAssignedTrue();


    @Query("SELECT i FROM Issue i WHERE i.id = :id AND i.assigned = false")
    Issue getUnassignedIssueByID(@Param("id") Long id);






}