package au.edu.aufonduebackend.service.impl;

import au.edu.aufonduebackend.model.entity.*;
import au.edu.aufonduebackend.repository.*;
import au.edu.aufonduebackend.service.IssueRemarkService;
import au.edu.aufonduebackend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class IssueRemarkServiceImpl implements IssueRemarkService {

    @Autowired
    private IssueRemarkRepository remarkRepository;

    @Autowired
    private IssueRemarkHistoryRepository historyRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Override
    public IssueRemark createRemark(Issue issue, IssueRemark.RemarkType remarkType, Admin createdBy) {
        IssueRemark remark = new IssueRemark();
        remark.setIssue(issue);
        remark.setRemarkType(remarkType);
        remark.setIsViewed(false);
        remark.setCreatedBy(createdBy);
        
        IssueRemark savedRemark = remarkRepository.save(remark);
        
        // Create history entry
        createHistoryEntry(issue, remarkType, issue.getStatus(), createdBy, IssueRemarkHistory.Action.CREATED);
        
        return savedRemark;
    }

    @Override
    public IssueRemark updateRemark(Long issueId, IssueRemark.RemarkType remarkType, String status, Admin updatedBy) throws Exception {
        // Validate the status-remark combination
        if (!validateStatusRemarkCombination(status, remarkType)) {
            throw new Exception(getValidationErrorMessage(status, remarkType));
        }
        
        Issue issue = issueRepository.findById(issueId)
            .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));
        
        Optional<IssueRemark> existingRemark = remarkRepository.findByIssueId(issueId);
        
        IssueRemark remark;
        if (existingRemark.isPresent()) {
            remark = existingRemark.get();
            remark.setRemarkType(remarkType);
            remark.setIsViewed(false);
        } else {
            remark = new IssueRemark();
            remark.setIssue(issue);
            remark.setRemarkType(remarkType);
            remark.setIsViewed(false);
            remark.setCreatedBy(updatedBy);
        }
        
        IssueRemark savedRemark = remarkRepository.save(remark);
        
        // Create history entry
        createHistoryEntry(issue, remarkType, status, updatedBy, 
            existingRemark.isPresent() ? IssueRemarkHistory.Action.UPDATED : IssueRemarkHistory.Action.CREATED);
        
        return savedRemark;
    }

    @Override
    public Optional<IssueRemark> getRemarkByIssueId(Long issueId) {
        return remarkRepository.findByIssueId(issueId);
    }

    @Override
    public List<IssueRemark> getRemarksByIssueIds(List<Long> issueIds) {
        return remarkRepository.findByIssueIds(issueIds);
    }

    @Override
    public void markRemarkAsViewed(Long issueId, Admin viewedBy) {
        Optional<IssueRemark> remarkOpt = remarkRepository.findByIssueId(issueId);
        if (remarkOpt.isPresent()) {
            IssueRemark remark = remarkOpt.get();
            // Only mark as viewed if it's a 'new' remark
            if (remark.getRemarkType() == IssueRemark.RemarkType.NEW) {
                remark.setIsViewed(true);
                remarkRepository.save(remark);
                
                // Create history entry
                Issue issue = issueRepository.findById(issueId).orElse(null);
                if (issue != null) {
                    createHistoryEntry(issue, remark.getRemarkType(), issue.getStatus(), 
                        viewedBy, IssueRemarkHistory.Action.VIEWED);
                }
            }
        }
    }

    @Override
    public List<IssueRemark> getNewUnviewedRemarks() {
        return remarkRepository.findNewUnviewedRemarks();
    }

    @Override
    public boolean validateStatusRemarkCombination(String status, IssueRemark.RemarkType remarkType) {
        // Rules:
        // 1. PENDING status: can have RF, PR, or NEW remarks
        // 2. IN_PROGRESS status: can have RF or PR remarks
        // 3. COMPLETED status: must have OK remark only
        // 4. NEW remark is allowed with PENDING or IN_PROGRESS
        
        status = status.toUpperCase();
        
        switch (status) {
            case "PENDING":
                return remarkType == IssueRemark.RemarkType.RF || 
                       remarkType == IssueRemark.RemarkType.PR ||
                       remarkType == IssueRemark.RemarkType.NEW;
            
            case "IN_PROGRESS":
                return remarkType == IssueRemark.RemarkType.RF || 
                       remarkType == IssueRemark.RemarkType.PR ||
                       remarkType == IssueRemark.RemarkType.NEW;
            
            case "COMPLETED":
                return remarkType == IssueRemark.RemarkType.OK;
            
            default:
                // For any other status, allow any remark except strict validation
                return true;
        }
    }

    @Override
    public void createInitialRemarkForNewIssue(Issue issue) {
        // Check if remark already exists
        Optional<IssueRemark> existingRemark = remarkRepository.findByIssue(issue);
        if (existingRemark.isEmpty()) {
            IssueRemark newRemark = new IssueRemark();
            newRemark.setIssue(issue);
            newRemark.setRemarkType(IssueRemark.RemarkType.NEW);
            newRemark.setIsViewed(false);
            remarkRepository.save(newRemark);
            
            // Create history entry
            createHistoryEntry(issue, IssueRemark.RemarkType.NEW, issue.getStatus(), 
                null, IssueRemarkHistory.Action.CREATED);
        }
    }

    @Override
    public List<IssueRemark> getAllRemarks() {
        return remarkRepository.findAll();
    }

    private void createHistoryEntry(Issue issue, IssueRemark.RemarkType remarkType, 
                                   String status, Admin changedBy, IssueRemarkHistory.Action action) {
        IssueRemarkHistory history = new IssueRemarkHistory();
        history.setIssue(issue);
        history.setRemarkType(remarkType);
        history.setStatusAtTime(status);
        history.setChangedBy(changedBy);
        history.setAction(action);
        historyRepository.save(history);
    }

    private String getValidationErrorMessage(String status, IssueRemark.RemarkType remarkType) {
        if ("COMPLETED".equalsIgnoreCase(status) && remarkType != IssueRemark.RemarkType.OK) {
            return "When status is COMPLETED, remark must be OK. You selected: " + remarkType;
        } else if (("PENDING".equalsIgnoreCase(status) || "IN_PROGRESS".equalsIgnoreCase(status)) 
                   && remarkType == IssueRemark.RemarkType.OK) {
            return "OK remark can only be used with COMPLETED status. Current status: " + status;
        }
        return "Invalid status and remark combination: Status=" + status + ", Remark=" + remarkType;
    }
}