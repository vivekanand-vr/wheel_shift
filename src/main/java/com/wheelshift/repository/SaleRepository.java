package com.wheelshift.repository;

import com.wheelshift.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findBySaleDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Sale> findByBuyerNameContainingIgnoreCase(String buyerName);
       
    List<Sale> findBySalePriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Sale> findByPaymentMethod(String paymentMethod);
    
    @Query("SELECT s FROM Sale s ORDER BY s.salePrice DESC")
    List<Sale> findTopSalesByPrice(org.springframework.data.domain.Pageable pageable);
    
    Sale findByCarId(Long carId);
    
    @Query("SELECT SUM(s.salePrice) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalSalesAmount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(s.totalCommission) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalCommission(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT s.paymentMethod, COUNT(s) FROM Sale s GROUP BY s.paymentMethod")
    List<Object[]> countSalesByPaymentMethod();

    @Query("SELECT FUNCTION('YEAR', s.saleDate) as year, FUNCTION('MONTH', s.saleDate) as month, " +
           "COUNT(s) as count, SUM(s.salePrice) as total " +
           "FROM Sale s " +
           "GROUP BY FUNCTION('YEAR', s.saleDate), FUNCTION('MONTH', s.saleDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> findMonthlySalesStatistics();
}