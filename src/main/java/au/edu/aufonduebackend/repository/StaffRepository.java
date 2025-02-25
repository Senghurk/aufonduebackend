package au.edu.aufonduebackend.repository;


import au.edu.aufonduebackend.model.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    @Query(value = """
            SELECT * FROM staff s
            ORDER BY s.id
            LIMIT :size OFFSET :offset
            """, nativeQuery = true)
    List<Staff> findAllStaff(
            @Param("size") int size,
            @Param("offset") int offset
    );

   }