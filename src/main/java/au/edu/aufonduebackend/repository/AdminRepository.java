package au.edu.aufonduebackend.repository;

import au.edu.aufonduebackend.model.entity.Admin;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;


//Handles database operations for maintenance issues (CRUD)
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
}
