package com.wheelshift.repository;

import com.wheelshift.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmail(String email);
    
    List<Employee> findByDepartment(String department);
    
    List<Employee> findByPosition(String position);
    
    List<Employee> findByStatus(String status);
    
    List<Employee> findByJoinDateAfter(LocalDate date);
    
    @Query("SELECT e FROM Employee e WHERE e.lastLogin >= :lastLoginDate")
    List<Employee> findByLastLoginAfter(@Param("lastLoginDate") LocalDateTime lastLoginDate);
    
    @Query("SELECT e FROM Employee e WHERE e.performance = :rating")
    List<Employee> findByPerformanceRating(@Param("rating") String rating);
    
    @Query("SELECT e FROM Employee e JOIN e.handledSales s GROUP BY e ORDER BY COUNT(s) DESC")
    List<Employee> findTopSalesPeople();
    
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department = :department")
    Long countByDepartment(@Param("department") String department);
    
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = :status")
    Long countByStatus(@Param("status") String status);
}