package com.wheelshift.repository;

import com.wheelshift.model.Car;
import com.wheelshift.projection.CarBasicDetails;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

    // Basic queries by car properties
    Optional<Car> findByVinNumber(String vinNumber);
    
    Optional<Car> findByRegistrationNumber(String registrationNumber);
    
    List<Car> findByYear(Integer year);
    
    List<Car> findByColor(String color);
    
    List<Car> findByCurrentStatus(String status);
    
    @Query("SELECT c FROM Car c")
    List<CarBasicDetails> findAllCarBasicDetails();
    
    @Query("SELECT c FROM Car c")
    Page<CarBasicDetails> findAllCarBasicDetails(Pageable pageable);
    
    // Queries by model properties
    List<Car> findByCarModel_Make(String make);
    
    List<Car> findByCarModel_Model(String model);
    
    List<Car> findByCarModel_MakeAndCarModel_Model(String make, String model);
    
    List<Car> findByCarModel_Variant(String variant);
    
    List<Car> findByCarModel_FuelType(String fuelType);
    
    List<Car> findByCarModel_BodyType(String bodyType);
    
    List<Car> findByCarModel_TransmissionType(String transmissionType);
    
    // Combined property queries
    List<Car> findByCarModel_MakeAndYear(String make, Integer year);
    
    List<Car> findByCarModel_MakeAndCarModel_ModelAndYear(String make, String model, Integer year);
    
    // Range queries
    List<Car> findByMileageBetween(BigDecimal minMileage, BigDecimal maxMileage);
    
    List<Car> findByPurchasePriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Car> findBySellingPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Car> findByPurchaseDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Location queries
    List<Car> findByStorageLocation_Id(Long locationId);
    
    List<Car> findByStorageLocation_Name(String locationName);
    
    // Advanced statistics queries
    @Query("SELECT COUNT(c) FROM Car c WHERE c.currentStatus = :status")
    Long countByStatus(@Param("status") String status);
    
    @Query("SELECT AVG(c.sellingPrice - c.purchasePrice) FROM Car c JOIN c.sale s")
    BigDecimal getAverageProfitMargin();
    
    @Query("SELECT c.carModel.make, COUNT(c) FROM Car c GROUP BY c.carModel.make ORDER BY COUNT(c) DESC")
    List<Object[]> getInventoryByMake();
    
    @Query("SELECT c.carModel.bodyType, COUNT(c) FROM Car c GROUP BY c.carModel.bodyType ORDER BY COUNT(c) DESC")
    List<Object[]> getInventoryByBodyType();
    
    @Query("SELECT YEAR(c.purchaseDate), COUNT(c) FROM Car c GROUP BY YEAR(c.purchaseDate) ORDER BY YEAR(c.purchaseDate)")
    List<Object[]> getCarPurchaseCountByYear();
    
    @Query("SELECT MONTH(c.purchaseDate), COUNT(c) FROM Car c WHERE YEAR(c.purchaseDate) = :year GROUP BY MONTH(c.purchaseDate) ORDER BY MONTH(c.purchaseDate)")
    List<Object[]> getCarPurchaseCountByMonth(@Param("year") Integer year);
    
    @Query("SELECT AVG(c.mileage) FROM Car c WHERE c.currentStatus = 'available'")
    BigDecimal getAverageMileageOfAvailableCars();
    
    @Query("SELECT c.carModel.make, c.carModel.model, AVG(c.mileage) FROM Car c GROUP BY c.carModel.make, c.carModel.model")
    List<Object[]> getAverageMileageByModel();
    
    // Full text search
    @Query("SELECT c FROM Car c WHERE " +
           "LOWER(c.vinNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.registrationNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.carModel.make) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.carModel.model) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.carModel.variant) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.color) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Car> searchCars(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Inventory turnover queries
    @Query("SELECT AVG(DATEDIFF(s.saleDate, c.purchaseDate)) FROM Car c JOIN c.sale s")
    Double getAverageDaysToSell();
    
    @Query("SELECT c.carModel.make, AVG(DATEDIFF(s.saleDate, c.purchaseDate)) FROM Car c JOIN c.sale s GROUP BY c.carModel.make")
    List<Object[]> getAverageDaysToSellByMake();
}