package com.wheelshift.service;

import com.wheelshift.dto.FinancialStatistics;
import com.wheelshift.dto.TransactionSearchCriteria;
import com.wheelshift.model.FinancialTransaction;
import com.wheelshift.repository.FinancialTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialTransactionService {

    private final FinancialTransactionRepository transactionRepository;

    /**
	 *	   _____ _____  _    _ _____  
	 *	  / ____|  __ \| |  | |  __ \ 
	 *	 | |    | |__) | |  | | |  | |
	 *	 | |    |  _  /| |  | | |  | |
	 *	 | |____| | \ \| |__| | |__| |
	 *	  \_____|_|  \_\\____/|_____/ 
	 *	                                                   
     *				CRUD OPERATIONS
     */

    public List<FinancialTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public Page<FinancialTransaction> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }
    
    public FinancialTransaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));
    }
    
    public FinancialTransaction saveTransaction(FinancialTransaction transaction) {
        return transactionRepository.save(transaction);
    }
    
    public FinancialTransaction updateTransaction(Long id, FinancialTransaction transactionDetails) {
        FinancialTransaction transaction = getTransactionById(id);
        
        transaction.setTransactionType(transactionDetails.getTransactionType());
        transaction.setAmount(transactionDetails.getAmount());
        transaction.setTransactionDate(transactionDetails.getTransactionDate());
        transaction.setDescription(transactionDetails.getDescription());
        transaction.setVendorName(transactionDetails.getVendorName());
        transaction.setReceiptUrl(transactionDetails.getReceiptUrl());
        
        // Don't change the car association
        
        return transactionRepository.save(transaction);
    }
    
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    /**
	 *	   _____ ______          _____   _____ _    _ 
	 *	  / ____|  ____|   /\   |  __ \ / ____| |  | |
	 *	 | (___ | |__     /  \  | |__) | |    | |__| |
	 *	  \___ \|  __|   / /\ \ |  _  /| |    |  __  |
	 *	  ____) | |____ / ____ \| | \ \| |____| |  | |
	 *	 |_____/|______/_/    \_\_|  \_\\_____|_|  |_|
	 *	                                              
	 *				SEARCH & FILTERS OPERATIONS
     */
    
    public List<FinancialTransaction> getTransactionsByCarId(Long carId) {
        return transactionRepository.findByCarId(carId);
    }
    
    public List<FinancialTransaction> getTransactionsByType(String type) {
        return transactionRepository.findByTransactionType(type);
    }
    
    public List<FinancialTransaction> getTransactionsInDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByTransactionDateBetween(startDate, endDate);
    }
    
    public List<FinancialTransaction> getTransactionsByTypeInDateRange(
            String type, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByTransactionTypeAndTransactionDateBetween(type, startDate, endDate);
    }
    
    public Page<FinancialTransaction> searchTransactions(TransactionSearchCriteria criteria, Pageable pageable) {
        return transactionRepository.searchTransactions(
                criteria.getTransactionType(),
                criteria.getStartDate(),
                criteria.getEndDate(),
                criteria.getMinAmount(),
                criteria.getMaxAmount(),
                criteria.getVendorName(),
                criteria.getCarId(),
                pageable
        );
    }

    /**
     *  	____ _______    _______ _____ 
	 *	  / ____|__   __|/\|__   __/ ____|
	 *	 | (___    | |  /  \  | | | (___  
	 *	  \___ \   | | / /\ \ | |  \___ \ 
	 *	  ____) |  | |/ ____ \| |  ____) |
	 *	 |_____/   |_/_/    \_\_| |_____/ 
	 *
	 *				STATISTICS AND ANALYTICS
     */

    public BigDecimal getTotalByTransactionType(String type) {
        return transactionRepository.getTotalAmountByType(type);
    }
    
    public BigDecimal getTotalInDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.getTotalAmountInDateRange(startDate, endDate);
    }
    
    public FinancialStatistics getFinancialStatistics() {
        FinancialStatistics statistics = new FinancialStatistics();
        
        // Total amounts by transaction type
        List<Object[]> totalsByType = transactionRepository.getTotalAmountByTransactionType();
        Map<String, BigDecimal> totalByType = totalsByType.stream()
                .collect(Collectors.toMap(
                    obj -> (String) obj[0],      // transaction type
                    obj -> (BigDecimal) obj[1]   // total amount
                ));
        statistics.setTotalByTransactionType(totalByType);
        
        // Calculate some common metrics if purchase and sale data exists
        BigDecimal totalPurchases = totalByType.getOrDefault("Purchase", BigDecimal.ZERO);
        BigDecimal totalSales = totalByType.getOrDefault("Sale", BigDecimal.ZERO);
        BigDecimal totalMaintenance = totalByType.getOrDefault("Maintenance", BigDecimal.ZERO);
        
        statistics.setTotalPurchases(totalPurchases);
        statistics.setTotalSales(totalSales);
        statistics.setTotalMaintenance(totalMaintenance);
        
        // Revenue (sales minus purchases and maintenance)
        BigDecimal revenue = totalSales.subtract(totalPurchases).subtract(totalMaintenance);
        statistics.setTotalRevenue(revenue);
        
        // Monthly totals for purchases
        List<Object[]> monthlyPurchases = transactionRepository.getMonthlyTotalByType("Purchase");
        Map<String, BigDecimal> purchasesByMonth = new HashMap<>();
        
        for (Object[] result : monthlyPurchases) {
            Integer year = (Integer) result[0];
            Integer month = (Integer) result[1];
            BigDecimal total = (BigDecimal) result[2];
            
            String yearMonth = year + "-" + (month < 10 ? "0" + month : month);
            purchasesByMonth.put(yearMonth, total);
        }
        statistics.setPurchasesByMonth(purchasesByMonth);
        
        // Monthly totals for sales
        List<Object[]> monthlySales = transactionRepository.getMonthlyTotalByType("Sale");
        Map<String, BigDecimal> salesByMonth = new HashMap<>();
        
        for (Object[] result : monthlySales) {
            Integer year = (Integer) result[0];
            Integer month = (Integer) result[1];
            BigDecimal total = (BigDecimal) result[2];
            
            String yearMonth = year + "-" + (month < 10 ? "0" + month : month);
            salesByMonth.put(yearMonth, total);
        }
        statistics.setSalesByMonth(salesByMonth);
        
        // Vendor spend analysis
        List<Object[]> vendorSpend = transactionRepository.getTotalSpendByVendor();
        Map<String, BigDecimal> spendByVendor = vendorSpend.stream()
                .collect(Collectors.toMap(
                    obj -> (String) obj[0],      // vendor name
                    obj -> (BigDecimal) obj[2]   // total amount
                ));
        statistics.setSpendByVendor(spendByVendor);
        
        // Spend by car make
        List<Object[]> makeSpend = transactionRepository.getTotalAmountByMakeAndType("Maintenance");
        Map<String, BigDecimal> maintenanceByMake = makeSpend.stream()
                .collect(Collectors.toMap(
                    obj -> (String) obj[0],      // make
                    obj -> (BigDecimal) obj[2]   // total amount
                ));
        statistics.setMaintenanceByMake(maintenanceByMake);
        
        return statistics;
    }
}