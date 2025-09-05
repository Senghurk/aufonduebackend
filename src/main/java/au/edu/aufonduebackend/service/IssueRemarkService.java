package au.edu.aufonduebackend.service;

import au.edu.aufonduebackend.model.entity.Issue;
import au.edu.aufonduebackend.model.entity.IssueRemark;
import au.edu.aufonduebackend.model.entity.Admin;
import java.util.List;
import java.util.Optional;

public interface IssueRemarkService {
    
    IssueRemark createRemark(Issue issue, IssueRemark.RemarkType remarkType, Admin createdBy);
    
    IssueRemark updateRemark(Long issueId, IssueRemark.RemarkType remarkType, String status, Admin updatedBy) throws Exception;
    
    Optional<IssueRemark> getRemarkByIssueId(Long issueId);
    
    List<IssueRemark> getRemarksByIssueIds(List<Long> issueIds);
    
    void markRemarkAsViewed(Long issueId, Admin viewedBy);
    
    List<IssueRemark> getNewUnviewedRemarks();
    
    boolean validateStatusRemarkCombination(String status, IssueRemark.RemarkType remarkType);
    
    void createInitialRemarkForNewIssue(Issue issue);
    
    List<IssueRemark> getAllRemarks();
}