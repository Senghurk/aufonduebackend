package au.edu.aufonduebackend.repository;
import au.edu.aufonduebackend.model.entity.Update;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

//Handles issue update records
@Repository
public interface UpdateRepository extends JpaRepository<Update, Long> {
   List<Update> findByIssueId(Long issueId);
   }