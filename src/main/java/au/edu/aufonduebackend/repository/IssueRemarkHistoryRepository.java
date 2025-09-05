package au.edu.aufonduebackend.repository;

import au.edu.aufonduebackend.model.entity.Issue;
import au.edu.aufonduebackend.model.entity.IssueRemarkHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IssueRemarkHistoryRepository extends JpaRepository<IssueRemarkHistory, Long> {
    
    List<IssueRemarkHistory> findByIssue(Issue issue);
    
    List<IssueRemarkHistory> findByIssueIdOrderByChangedAtDesc(Long issueId);
    
    @Query("SELECT irh FROM IssueRemarkHistory irh WHERE irh.issue.id = :issueId AND irh.changedAt BETWEEN :startDate AND :endDate")
    List<IssueRemarkHistory> findByIssueIdAndDateRange(@Param("issueId") Long issueId, 
                                                         @Param("startDate") LocalDateTime startDate, 
                                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT irh FROM IssueRemarkHistory irh WHERE irh.issue.status = 'COMPLETED'")
    List<IssueRemarkHistory> findCompletedIssuesHistory();
    
    List<IssueRemarkHistory> findByAction(IssueRemarkHistory.Action action);
    
    void deleteByIssue(Issue issue);
    
    void deleteByIssueId(Long issueId);
}