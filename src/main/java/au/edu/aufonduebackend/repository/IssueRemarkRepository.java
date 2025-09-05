package au.edu.aufonduebackend.repository;

import au.edu.aufonduebackend.model.entity.Issue;
import au.edu.aufonduebackend.model.entity.IssueRemark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRemarkRepository extends JpaRepository<IssueRemark, Long> {
    
    Optional<IssueRemark> findByIssue(Issue issue);
    
    Optional<IssueRemark> findByIssueId(Long issueId);
    
    List<IssueRemark> findByRemarkType(IssueRemark.RemarkType remarkType);
    
    List<IssueRemark> findByIsViewedFalse();
    
    @Query("SELECT ir FROM IssueRemark ir WHERE ir.issue.id IN :issueIds")
    List<IssueRemark> findByIssueIds(@Param("issueIds") List<Long> issueIds);
    
    @Query("SELECT ir FROM IssueRemark ir WHERE ir.remarkType = 'NEW' AND ir.isViewed = false")
    List<IssueRemark> findNewUnviewedRemarks();
    
    void deleteByIssue(Issue issue);
}