package com.wheelshift.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.wheelshift.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {
    
    Optional<Client> findByEmail(String email);
    
    Page<Client> findAll(Pageable pageable);
    
    List<Client> findByStatus(String status);
    
    List<Client> findByTotalPurchasesGreaterThan(Integer purchases);
    
    List<Client> findByLastPurchaseAfter(LocalDate date);
    
    @Query("SELECT c FROM Client c WHERE c.totalPurchases > 0 ORDER BY c.totalPurchases DESC")
    List<Client> findTopBuyers();
    
    @Query("SELECT c FROM Client c WHERE c.lastPurchase >= :startDate AND c.lastPurchase <= :endDate")
    List<Client> findByPurchaseDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT c FROM Client c WHERE c.location = :location")
    List<Client> findByLocation(@Param("location") String location);
    
    @Query("SELECT COUNT(c) FROM Client c WHERE c.status = :status")
    Long countByStatus(@Param("status") String status);
    
    @Query("SELECT COUNT(c) FROM Client c WHERE c.createdAt >= :startDate")
    Long countNewClientsAfter(@Param("startDate") LocalDate startDate);
}