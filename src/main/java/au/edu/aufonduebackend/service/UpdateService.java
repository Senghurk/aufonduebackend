package au.edu.aufonduebackend.service;


import au.edu.aufonduebackend.model.dto.request.UpdateRequest;
import au.edu.aufonduebackend.model.dto.response.UpdateResponse;
import au.edu.aufonduebackend.model.entity.Update;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UpdateService {
     UpdateResponse createUpdate(UpdateRequest request, List<MultipartFile> photos);
     UpdateResponse changeUpdateStatus(Long updateId, String newStatus);
     List<UpdateResponse> getUpdatesByIssueId(Long issueId);
}