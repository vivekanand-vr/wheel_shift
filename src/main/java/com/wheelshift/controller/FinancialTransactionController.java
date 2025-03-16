package com.wheelshift.controller;

import com.wheelshift.dto.FinancialStatistics;
import com.wheelshift.dto.TransactionSearchCriteria;
import com.wheelshift.model.FinancialTransaction;
import com.wheelshift.service.FinancialTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class FinancialTransactionController {

    private final FinancialTransactionService transactionService;

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

    @GetMapping
    public ResponseEntity<List<FinancialTransaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<FinancialTransaction>> getAllTransactionsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(transactionService.getAllTransactions(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialTransaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PostMapping
    public ResponseEntity<FinancialTransaction> createTransaction(
            @Valid @RequestBody FinancialTransaction transaction) {
        return new ResponseEntity<>(transactionService.saveTransaction(transaction), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialTransaction> updateTransaction(
            @PathVariable Long id, 
            @Valid @RequestBody FinancialTransaction transaction) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
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
    
    @GetMapping("/car/{carId}")
    public ResponseEntity<List<FinancialTransaction>> getTransactionsByCarId(@PathVariable Long carId) {
        return ResponseEntity.ok(transactionService.getTransactionsByCarId(carId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<FinancialTransaction>> getTransactionsByType(@PathVariable String type) {
        return ResponseEntity.ok(transactionService.getTransactionsByType(type));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<FinancialTransaction>> getTransactionsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
    	return ResponseEntity.ok(transactionService.getTransactionsInDateRange(startDate, endDate));
    }
    
    @GetMapping("/date-range/type")
    public ResponseEntity<List<FinancialTransaction>> getTransactionsByTypeInDateRange(
            @RequestParam String type, 
    		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
    	return ResponseEntity.ok(transactionService.getTransactionsByTypeInDateRange(type, startDate, endDate));
    }
    
    @PostMapping("/search")
    public ResponseEntity<Page<FinancialTransaction>> getTransactionsBySearch(
    		@RequestBody TransactionSearchCriteria transactionSearchCriteria,
    		@PageableDefault(size = 10, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable){
    	return ResponseEntity.ok(transactionService.searchTransactions(transactionSearchCriteria, pageable));
    }
    
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalByTransactionType(@RequestParam String type) {
        return ResponseEntity.ok(transactionService.getTotalByTransactionType(type));
    }

    @GetMapping("/total-date-range")
    public ResponseEntity<BigDecimal> getTotalInDateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(transactionService.getTotalInDateRange(startDate, endDate));
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
    
    @GetMapping("/statistics")
    public ResponseEntity<FinancialStatistics> getFinancialStatistics() {
        return ResponseEntity.ok(transactionService.getFinancialStatistics());
    }
}
            
            
            
            