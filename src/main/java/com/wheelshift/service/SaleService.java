package com.wheelshift.service;

import com.wheelshift.model.Car;
import com.wheelshift.model.Client;
import com.wheelshift.model.Employee;
import com.wheelshift.model.Sale;
import com.wheelshift.repository.CarRepository;
import com.wheelshift.repository.ClientRepository;
import com.wheelshift.repository.EmployeeRepository;
import com.wheelshift.repository.SaleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final CarRepository carRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    public SaleService(SaleRepository saleRepository, CarRepository carRepository,
                       ClientRepository clientRepository, EmployeeRepository employeeRepository) {
        this.saleRepository = saleRepository;
        this.carRepository = carRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
    }
    
    /**
     *     _____ _____  _    _ _____  
     *    / ____|  __ \| |  | |  __ \ 
     *   | |    | |__) | |  | | |  | |
     *   | |    |  _  /| |  | | |  | |
     *   | |____| | \ \| |__| | |__| |
     *    \_____|_|  \_\\____/|_____/ 
     *                                                   
     *              CRUD OPERATIONS
     */
    
    public Optional<Sale> getSaleById(Long id) {
        return saleRepository.findById(id);
    }
    
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }
    
    @Transactional
    public Sale createSale(Sale sale) {
        // Validate car exists and is not already sold
        Car car = carRepository.findById(sale.getCar().getId())
                .orElseThrow(() -> new IllegalArgumentException("Car not found with ID: " + sale.getCar().getId()));
        
        if (car.getSale() != null) {
            throw new IllegalStateException("Car is already sold");
        }
        
        // Validate client exists
        Client client = clientRepository.findById(sale.getClient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + sale.getClient().getId()));
        
        // Validate employee exists
        @SuppressWarnings("unused")
		Employee employee = employeeRepository.findById(sale.getHandledBy().getId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + sale.getHandledBy().getId()));
        
        // Calculate commission if not set
        if (sale.getCommissionRate() != null && sale.getTotalCommission() == null) {
            BigDecimal commissionAmount = sale.getSalePrice()
                    .multiply(sale.getCommissionRate().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            sale.setTotalCommission(commissionAmount);
        }
        
        // Update car status to sold
        car.setCurrentStatus("SOLD");
        carRepository.save(car);
        
        // Update client's purchase information
        client.setTotalPurchases(client.getTotalPurchases() + 1);
        client.setLastPurchase(sale.getSaleDate());
        clientRepository.save(client);
        
        return saleRepository.save(sale);
    }
    
    @Transactional
    public Sale updateSale(Long id, Sale saleDetails) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found with ID: " + id));
        
        // Update sale price and payment method
        sale.setSalePrice(saleDetails.getSalePrice());
        sale.setPaymentMethod(saleDetails.getPaymentMethod());
        sale.setSaleDocumentsUrl(saleDetails.getSaleDocumentsUrl());
        
        // Optionally update sale date
        if (saleDetails.getSaleDate() != null) {
            sale.setSaleDate(saleDetails.getSaleDate());
        }
        
        // Update client if changed
        if (saleDetails.getClient() != null && !saleDetails.getClient().getId().equals(sale.getClient().getId())) {
            Client newClient = clientRepository.findById(saleDetails.getClient().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + saleDetails.getClient().getId()));
            
            // Update old client's purchase information
            Client oldClient = sale.getClient();
            oldClient.setTotalPurchases(oldClient.getTotalPurchases() - 1);
            clientRepository.save(oldClient);
            
            // Update new client's purchase information
            newClient.setTotalPurchases(newClient.getTotalPurchases() + 1);
            newClient.setLastPurchase(sale.getSaleDate());
            clientRepository.save(newClient);
            
            sale.setClient(newClient);
        }
        
        // Update employee if changed
        if (saleDetails.getHandledBy() != null && !saleDetails.getHandledBy().getId().equals(sale.getHandledBy().getId())) {
            Employee newEmployee = employeeRepository.findById(saleDetails.getHandledBy().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + saleDetails.getHandledBy().getId()));
            sale.setHandledBy(newEmployee);
        }
        
        // Recalculate commission if needed
        if (saleDetails.getCommissionRate() != null) {
            sale.setCommissionRate(saleDetails.getCommissionRate());
            BigDecimal commissionAmount = sale.getSalePrice()
                    .multiply(sale.getCommissionRate().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            sale.setTotalCommission(commissionAmount);
        }
        
        return saleRepository.save(sale);
    }

    @Transactional
    public void deleteSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found with ID: " + id));
        
        // Update car status back to available
        Car car = sale.getCar();
        car.setCurrentStatus("AVAILABLE");
        carRepository.save(car);
        
        // Update client's purchase information
        Client client = sale.getClient();
        client.setTotalPurchases(client.getTotalPurchases() - 1);
        
        // Update last purchase date if needed
        if (client.getLastPurchase() != null && client.getLastPurchase().equals(sale.getSaleDate())) {
            // Find the next most recent purchase
            Optional<Sale> latestSale = saleRepository.findTopByClientIdAndIdNotOrderBySaleDateDesc(client.getId(), id);
            client.setLastPurchase(latestSale.map(Sale::getSaleDate).orElse(null));
        }
        
        clientRepository.save(client);
        
        saleRepository.delete(sale);
    }
    
    /**
     *     _____ ______          _____   _____ _    _ 
     *    / ____|  ____|   /\   |  __ \ / ____| |  | |
     *   | (___ | |__     /  \  | |__) | |    | |__| |
     *    \___ \|  __|   / /\ \ |  _  /| |    |  __  |
     *    ____) | |____ / ____ \| | \ \| |____| |  | |
     *   |_____/|______/_/    \_\_|  \_\\_____|_|  |_|
     *                                              
     *              SEARCH & FILTERS OPERATIONS
     */

    public Page<Sale> getAllSalesPaginated(Pageable pageable) {
        return saleRepository.findAll(pageable);
    }
    
    public List<Sale> findSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        return saleRepository.findBySaleDateBetween(startDate, endDate);
    }
    
    public List<Sale> findSalesByClient(Long clientId) {
        return saleRepository.findByClientId(clientId);
    }
    
    public List<Sale> findSalesByEmployee(Long employeeId) {
        return saleRepository.findByHandledById(employeeId);
    }

    public List<Sale> findSalesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return saleRepository.findBySalePriceBetween(minPrice, maxPrice);
    }

    public List<Sale> findTopSalesByPrice(int limit) {
        return saleRepository.findTopSalesByPrice(PageRequest.of(0, limit));
    }
    
    /**
     *     ____ _______    _______ _____ 
     *    / ____|__   __|/\|__   __/ ____|
     *   | (___    | |  /  \  | | | (___  
     *    \___ \   | | / /\ \ | |  \___ \ 
     *    ____) |  | |/ ____ \| |  ____) |
     *   |_____/   |_/_/    \_\_| |_____/ 
     *
     *              STATISTICS AND ANALYTICS
     */

    public BigDecimal calculateTotalSalesAmount(LocalDate startDate, LocalDate endDate) {
        BigDecimal total = saleRepository.calculateTotalSalesAmount(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal calculateTotalCommission(LocalDate startDate, LocalDate endDate) {
        BigDecimal total = saleRepository.calculateTotalCommission(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Map<String, Long> getSalesByPaymentMethod() {
        List<Object[]> results = saleRepository.countSalesByPaymentMethod();
        Map<String, Long> distribution = new HashMap<>();
        
        for (Object[] result : results) {
            String paymentMethod = (String) result[0];
            Long count = (Long) result[1];
            distribution.put(paymentMethod, count);
        }
        
        return distribution;
    }

    public List<Map<String, Object>> getMonthlySalesStatistics() {
        List<Object[]> results = saleRepository.findMonthlySalesStatistics();
        return results.stream().map(row -> {
            Map<String, Object> stat = new HashMap<>();
            stat.put("year", row[0]);
            stat.put("month", row[1]);
            stat.put("count", row[2]);
            stat.put("totalAmount", row[3]);
            return stat;
        }).toList();
    }
    
    public BigDecimal calculateAverageSalePrice() {
        List<Sale> sales = saleRepository.findAll();
        if (sales.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = sales.stream()
                .map(Sale::getSalePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        return total.divide(new BigDecimal(sales.size()), 2, RoundingMode.HALF_UP);
    }
    
    public Map<String, BigDecimal> calculateProfitMargin(LocalDate startDate, LocalDate endDate) {
        List<Sale> sales = saleRepository.findBySaleDateBetween(startDate, endDate);
        
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        
        for (Sale sale : sales) {
            totalRevenue = totalRevenue.add(sale.getSalePrice());
            totalCost = totalCost.add(sale.getCar().getPurchasePrice() != null ? 
                    sale.getCar().getPurchasePrice() : BigDecimal.ZERO);
        }
        
        BigDecimal profit = totalRevenue.subtract(totalCost);
        BigDecimal profitMargin = BigDecimal.ZERO;
        
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            profitMargin = profit.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("totalRevenue", totalRevenue);
        result.put("totalCost", totalCost);
        result.put("profit", profit);
        result.put("profitMargin", profitMargin);
        
        return result;
    }
    
    public Map<Integer, BigDecimal> getYearlySalesPerformance() {
        List<Sale> sales = saleRepository.findAll();
        Map<Integer, BigDecimal> yearlyPerformance = new HashMap<>();
        
        for (Sale sale : sales) {
            int year = sale.getSaleDate().getYear();
            BigDecimal saleAmount = sale.getSalePrice();
            
            yearlyPerformance.put(year, 
                    yearlyPerformance.getOrDefault(year, BigDecimal.ZERO).add(saleAmount));
        }
        
        return yearlyPerformance;
    }
    
    public Map<Long, Integer> getTopSalespersons(int limit) {
        List<Object[]> results = saleRepository.findTopSellingEmployees(PageRequest.of(0, limit));
        Map<Long, Integer> topSalespersons = new HashMap<>();
        
        for (Object[] result : results) {
            Long employeeId = (Long) result[0];
            Integer salesCount = ((Number) result[1]).intValue();
            topSalespersons.put(employeeId, salesCount);
        }
        
        return topSalespersons;
    }
    
    public Map<Long, BigDecimal> getTopClients(int limit) {
        List<Object[]> results = saleRepository.findTopClients(PageRequest.of(0, limit));
        Map<Long, BigDecimal> topClients = new HashMap<>();
        
        for (Object[] result : results) {
            Long clientId = (Long) result[0];
            BigDecimal totalSpent = (BigDecimal) result[1];
            topClients.put(clientId, totalSpent);
        }
        
        return topClients;
    }
}