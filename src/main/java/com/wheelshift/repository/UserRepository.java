package com.wheelshift.repository;

import com.wheelshift.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByIsActive(Boolean isActive);

    List<User> findByFullNameContainingIgnoreCase(String fullName);

    @Query("SELECT u FROM User u WHERE u.lastLogin < :date OR u.lastLogin IS NULL")
    List<User> findInactiveUsersSince(@Param("date") LocalDateTime date);
  
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u, COUNT(i) FROM User u LEFT JOIN u.assignedInquiries i GROUP BY u")
    List<Object[]> findUsersWithInquiryCount();

    @Query("SELECT u, COUNT(i) FROM User u JOIN u.assignedInquiries i " +
           "GROUP BY u ORDER BY COUNT(i) DESC")
    List<Object[]> findTopUsersByInquiryCount(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u JOIN u.assignedInquiries i WHERE i.status = :status")
    List<User> findUsersWithInquiriesInStatus(@Param("status") String status);
}