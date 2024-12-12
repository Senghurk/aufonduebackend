// File: src/main/java/com/aufondue/repository/IssueRepository.java

package au.edu.aufonduebackend.repository;

import au.edu.aufonduebackend.model.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByStatus(String status);

    @Query(value = """
            SELECT * FROM issues i
            WHERE ST_DWithin(
                ST_MakePoint(i.longitude, i.latitude)::geography,
                ST_MakePoint(:longitude, :latitude)::geography,
                :radiusMeters
            )
            """, nativeQuery = true)
    List<Issue> findNearbyIssues(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusMeters") Double radiusMeters
    );
}