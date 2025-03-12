package com.wheelshift.repository;

import com.wheelshift.model.Car;
import com.wheelshift.model.Inquiry;
import com.wheelshift.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    
    List<Inquiry> findByCar(Car car);
    
    List<Inquiry> findByStatus(String status);
    
    List<Inquiry> findByAssignedTo(User user);
    
    List<Inquiry> findByCustomerEmailContainingIgnoreCase(String email);
    
    List<Inquiry> findByCustomerNameContainingIgnoreCase(String name);
    
    List<Inquiry> findByInquiryType(String inquiryType);
    
    List<Inquiry> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COUNT(i) FROM Inquiry i WHERE i.status = :status")
    Long countByStatus(String status);
    
    @Query("SELECT i.inquiryType, COUNT(i) FROM Inquiry i GROUP BY i.inquiryType")
    List<Object[]> countByInquiryType();
    
    @Query("SELECT i FROM Inquiry i WHERE i.status = 'PENDING' ORDER BY i.createdAt ASC")
    List<Inquiry> findPendingInquiriesSortedByCreationDate();
    
    @Query("SELECT i FROM Inquiry i WHERE i.car.id = :carId AND i.status != 'CLOSED'")
    List<Inquiry> findActiveInquiriesByCar(Long carId);
}
