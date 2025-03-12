package com.wheelshift.repository;

import com.wheelshift.model.FinancialTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
    
    // Basic queries
    List<FinancialTransaction> findByCarId(Long carId);
    
    List<FinancialTransaction> findByTransactionType(String transactionType);
    
    List<FinancialTransaction> findByVendorName(String vendorName);
    
    // Date range queries
    List<FinancialTransaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Amount range queries
    List<FinancialTransaction> findByAmountGreaterThanEqual(BigDecimal minAmount);
    
    List<FinancialTransaction> findByAmountLessThanEqual(BigDecimal maxAmount);
    
    List<FinancialTransaction> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    // Combined queries
    List<FinancialTransaction> findByTransactionTypeAndTransactionDateBetween(
            String transactionType, LocalDate startDate, LocalDate endDate);
    
    // Advanced search
    @Query("SELECT t FROM FinancialTransaction t WHERE " +
           "(:transactionType IS NULL OR t.transactionType = :transactionType) AND " +
           "(:startDate IS NULL OR t.transactionDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transactionDate <= :endDate) AND " +
           "(:minAmount IS NULL OR t.amount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR t.amount <= :maxAmount) AND " +
           "(:vendorName IS NULL OR t.vendorName LIKE %:vendorName%) AND " +
           "(:carId IS NULL OR t.car.id = :carId)")
    Page<FinancialTransaction> searchTransactions(
            @Param("transactionType") String transactionType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("vendorName") String vendorName,
            @Param("carId") Long carId,
            Pageable pageable);
    
    // Statistics queries
    @Query("SELECT SUM(t.amount) FROM FinancialTransaction t WHERE t.transactionType = :transactionType")
    BigDecimal getTotalAmountByType(@Param("transactionType") String transactionType);
    
    @Query("SELECT SUM(t.amount) FROM FinancialTransaction t " +
           "WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalAmountInDateRange(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t.transactionType, SUM(t.amount) " +
           "FROM FinancialTransaction t " +
           "GROUP BY t.transactionType " +
           "ORDER BY SUM(t.amount) DESC")
    List<Object[]> getTotalAmountByTransactionType();
    
    @Query("SELECT FUNCTION('YEAR', t.transactionDate) as year, " +
           "FUNCTION('MONTH', t.transactionDate) as month, " +
           "SUM(t.amount) as total " +
           "FROM FinancialTransaction t " +
           "WHERE t.transactionType = :transactionType " +
           "GROUP BY FUNCTION('YEAR', t.transactionDate), FUNCTION('MONTH', t.transactionDate) " +
           "ORDER BY year, month")
    List<Object[]> getMonthlyTotalByType(@Param("transactionType") String transactionType);
    
    @Query("SELECT t.vendorName, COUNT(t), SUM(t.amount) " +
           "FROM FinancialTransaction t " +
           "WHERE t.vendorName IS NOT NULL " +
           "GROUP BY t.vendorName " +
           "ORDER BY SUM(t.amount) DESC")
    List<Object[]> getTotalSpendByVendor();
    
    @Query("SELECT cm.make, COUNT(t), SUM(t.amount) " +
           "FROM FinancialTransaction t " +
           "JOIN t.car c " +
           "JOIN c.carModel cm " +
           "WHERE t.transactionType = :transactionType " +
           "GROUP BY cm.make " +
           "ORDER BY SUM(t.amount) DESC")
    List<Object[]> getTotalAmountByMakeAndType(@Param("transactionType") String transactionType);
}