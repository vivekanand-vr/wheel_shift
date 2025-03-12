package com.wheelshift.controller;

import com.wheelshift.model.Sale;
import com.wheelshift.service.SaleService;
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
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        Optional<Sale> sale = saleService.getSaleById(id);
        return sale.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Sale>> getAllSales() {
        List<Sale> sales = saleService.getAllSales();
        return ResponseEntity.ok(sales);
    }

    @PostMapping
    public ResponseEntity<Sale> createSale(@RequestBody Sale sale) {
        try {
            Sale createdSale = saleService.createSale(sale);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSale);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sale> updateSale(@PathVariable Long id, @RequestBody Sale saleDetails) {
        try {
            Sale updatedSale = saleService.updateSale(id, saleDetails);
            return ResponseEntity.ok(updatedSale);
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
    
    @GetMapping("/search/date-range")
    public ResponseEntity<List<Sale>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Sale> sales = saleService.findSalesByDateRange(startDate, endDate);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/search/buyer")
    public ResponseEntity<List<Sale>> getSalesByBuyerName(@RequestParam String buyerName) {
        List<Sale> sales = saleService.findSalesByBuyerName(buyerName);
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
}