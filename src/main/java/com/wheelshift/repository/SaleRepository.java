package com.wheelshift.repository;

import com.wheelshift.model.Sale;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    // Existing query methods
    List<Sale> findBySaleDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Sale> findByClientId(Long clientId);
    
    List<Sale> findByHandledById(Long employeeId);
    
    List<Sale> findBySalePriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    @Query("SELECT s FROM Sale s ORDER BY s.salePrice DESC")
    List<Sale> findTopSalesByPrice(Pageable pageable);
    
    Optional<Sale> findTopByClientIdAndIdNotOrderBySaleDateDesc(Long clientId, Long saleId);
    
    @Query("SELECT SUM(s.salePrice) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalSalesAmount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(s.totalCommission) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalCommission(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT s.paymentMethod, COUNT(s) FROM Sale s GROUP BY s.paymentMethod")
    List<Object[]> countSalesByPaymentMethod();
    
    @Query("SELECT YEAR(s.saleDate), MONTH(s.saleDate), COUNT(s), SUM(s.salePrice) " +
           "FROM Sale s GROUP BY YEAR(s.saleDate), MONTH(s.saleDate) " +
           "ORDER BY YEAR(s.saleDate), MONTH(s.saleDate)")
    List<Object[]> findMonthlySalesStatistics();
    
    // New query method for finding top selling employees
    @Query("SELECT s.handledBy.id, COUNT(s) AS salesCount " +
           "FROM Sale s " +
           "GROUP BY s.handledBy.id " +
           "ORDER BY salesCount DESC")
    List<Object[]> findTopSellingEmployees(Pageable pageable);
    
    // New query method for finding top clients by total amount spent
    @Query("SELECT s.client.id, SUM(s.salePrice) AS totalSpent " +
           "FROM Sale s " +
           "GROUP BY s.client.id " +
           "ORDER BY totalSpent DESC")
    List<Object[]> findTopClients(Pageable pageable);
    
    // Query for finding top clients by number of purchases
    @Query("SELECT s.client.id, COUNT(s) AS purchaseCount " +
           "FROM Sale s " +
           "GROUP BY s.client.id " +
           "ORDER BY purchaseCount DESC")
    List<Object[]> findTopClientsByPurchaseCount(Pageable pageable);
    
    // Query for finding top selling employees with their commission totals
    @Query("SELECT s.handledBy.id, COUNT(s) AS salesCount, SUM(s.totalCommission) AS totalCommission " +
           "FROM Sale s " +
           "WHERE s.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY s.handledBy.id " +
           "ORDER BY totalCommission DESC")
    List<Object[]> findTopSellingEmployeesByCommission(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate, 
            Pageable pageable);
    
    // Query for finding car models with highest sales volume
    @Query("SELECT s.car.carModel.id, COUNT(s) AS salesCount " +
           "FROM Sale s " +
           "GROUP BY s.car.carModel.id " +
           "ORDER BY salesCount DESC")
    List<Object[]> findTopSellingCarModels(Pageable pageable);
    
    // Query for finding average time from car purchase to sale
    @Query("SELECT AVG(DATEDIFF(s.saleDate, s.car.purchaseDate)) " +
           "FROM Sale s " +
           "WHERE s.car.purchaseDate IS NOT NULL")
    Double findAverageDaysFromPurchaseToSale();
}