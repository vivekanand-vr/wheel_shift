package com.wheelshift.controller;

import com.wheelshift.dto.SaleDTO;
import com.wheelshift.model.Sale;
import com.wheelshift.service.SaleService;
import com.wheelshift.util.SaleMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<SaleDTO> getSaleById(@PathVariable Long id) {
        Optional<Sale> sale = saleService.getSaleById(id);
        return sale.map(s -> ResponseEntity.ok(SaleMapper.toDTO(s)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SaleDTO>> getAllSales() {
        List<Sale> sales = saleService.getAllSales();
        return ResponseEntity.ok(SaleMapper.toDTOList(sales));
    }

    @PostMapping
    public ResponseEntity<SaleDTO> createSale(@RequestBody Sale sale) {
        try {
            Sale createdSale = saleService.createSale(sale);
            return ResponseEntity.status(HttpStatus.CREATED).body(SaleMapper.toDTO(createdSale));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleDTO> updateSale(@PathVariable Long id, @RequestBody Sale saleDetails) {
        try {
            Sale updatedSale = saleService.updateSale(id, saleDetails);
            return ResponseEntity.ok(SaleMapper.toDTO(updatedSale));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        try {
            saleService.deleteSale(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
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
    
    @GetMapping("/paged")
    public ResponseEntity<Page<SaleDTO>> getAllSalesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "saleDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Sale> salesPage = saleService.getAllSalesPaginated(pageable);
        
        Page<SaleDTO> salesDtoPage = SaleMapper.toDTOPage(salesPage);
        return ResponseEntity.ok(salesDtoPage);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<SaleDTO>> searchSales(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "saleDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        // This is a placeholder for a more comprehensive search implementation
        // For now, just return paginated results
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Sale> salesPage = saleService.getAllSalesPaginated(pageable);
        
        // In a real implementation, you would use the search parameters
        // to filter the results before converting to DTOs
        
        Page<SaleDTO> salesDtoPage = SaleMapper.toDTOPage(salesPage);
        return ResponseEntity.ok(salesDtoPage);
    }
    
    @GetMapping("/search/date-range")
    public ResponseEntity<List<Sale>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Sale> sales = saleService.findSalesByDateRange(startDate, endDate);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/search/client")
    public ResponseEntity<List<Sale>> getSalesByClient(@RequestParam Long clientId) {
        List<Sale> sales = saleService.findSalesByClient(clientId);
        return ResponseEntity.ok(sales);
    }
    
    @GetMapping("/search/employee")
    public ResponseEntity<List<Sale>> getSalesByEmployee(@RequestParam Long employeeId) {
        List<Sale> sales = saleService.findSalesByEmployee(employeeId);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/search/price-range")
    public ResponseEntity<List<Sale>> getSalesByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        List<Sale> sales = saleService.findSalesByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/top")
    public ResponseEntity<List<Sale>> getTopSalesByPrice(@RequestParam(defaultValue = "5") int limit) {
        List<Sale> sales = saleService.findTopSalesByPrice(limit);
        return ResponseEntity.ok(sales);
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
     
     
    @GetMapping("/statistics/total-amount")
    public ResponseEntity<BigDecimal> getTotalSalesAmount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal total = saleService.calculateTotalSalesAmount(startDate, endDate);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/statistics/total-commission")
    public ResponseEntity<BigDecimal> getTotalCommission(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal totalCommission = saleService.calculateTotalCommission(startDate, endDate);
        return ResponseEntity.ok(totalCommission);
    }

    @GetMapping("/statistics/payment-methods")
    public ResponseEntity<Map<String, Long>> getSalesByPaymentMethod() {
        Map<String, Long> distribution = saleService.getSalesByPaymentMethod();
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/statistics/monthly")
    public ResponseEntity<List<Map<String, Object>>> getMonthlySalesStatistics() {
        List<Map<String, Object>> statistics = saleService.getMonthlySalesStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/statistics/average-price")
    public ResponseEntity<BigDecimal> getAverageSalePrice() {
        BigDecimal averagePrice = saleService.calculateAverageSalePrice();
        return ResponseEntity.ok(averagePrice);
    }

    @GetMapping("/statistics/profit-margin")
    public ResponseEntity<Map<String, BigDecimal>> getProfitMargin(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, BigDecimal> profitMargin = saleService.calculateProfitMargin(startDate, endDate);
        return ResponseEntity.ok(profitMargin);
    }

    @GetMapping("/statistics/yearly-performance")
    public ResponseEntity<Map<Integer, BigDecimal>> getYearlySalesPerformance() {
        Map<Integer, BigDecimal> yearlyPerformance = saleService.getYearlySalesPerformance();
        return ResponseEntity.ok(yearlyPerformance);
    }
    
    @GetMapping("/statistics/top-sales-persons")
    public ResponseEntity<Map<Long, Integer>> getTopSalespersons(int limit) {
        Map<Long,Integer> results = saleService.getTopSalespersons(limit);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/statistics/top-clients")
    public ResponseEntity<Map<Long, BigDecimal>> getTopClients(int limit) {
        Map<Long, BigDecimal> results = saleService.getTopClients(limit);
        return ResponseEntity.ok(results);
    }
}