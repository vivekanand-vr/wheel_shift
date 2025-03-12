package com.wheelshift.repository;

import com.wheelshift.model.StorageLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageLocationRepository extends JpaRepository<StorageLocation, Long> {
    
    // Basic queries
    List<StorageLocation> findByName(String name);
    
    List<StorageLocation> findByContactPerson(String contactPerson);
    
    // Available capacity check
    @Query("SELECT s FROM StorageLocation s WHERE (s.totalCapacity - s.currentVehicleCount) > 0")
    List<StorageLocation> findLocationsWithAvailableCapacity();
    
    @Query("SELECT s FROM StorageLocation s WHERE (s.totalCapacity - s.currentVehicleCount) >= :requiredSpace")
    List<StorageLocation> findLocationsWithCapacityAtLeast(Integer requiredSpace);
    
    // Statistics queries
    @Query("SELECT COUNT(s) FROM StorageLocation s WHERE s.currentVehicleCount = 0")
    Long countEmptyLocations();
    
    @Query("SELECT COUNT(s) FROM StorageLocation s WHERE s.currentVehicleCount = s.totalCapacity")
    Long countFullLocations();
    
    @Query("SELECT SUM(s.totalCapacity) FROM StorageLocation s")
    Integer getTotalCapacity();
    
    @Query("SELECT SUM(s.currentVehicleCount) FROM StorageLocation s")
    Integer getTotalVehiclesStored();
    
    @Query("SELECT AVG((s.currentVehicleCount * 100.0) / s.totalCapacity) FROM StorageLocation s WHERE s.totalCapacity > 0")
    Double getAverageOccupancyPercentage();
    
    @Query("SELECT s.name, s.currentVehicleCount, s.totalCapacity, " +
           "(s.currentVehicleCount * 100.0 / s.totalCapacity) as occupancyPercentage " +
           "FROM StorageLocation s " +
           "WHERE s.totalCapacity > 0 " +
           "ORDER BY occupancyPercentage DESC")
    List<Object[]> getLocationOccupancyRates();
    
    // Analytics for car types by location
    @Query("SELECT s.id, s.name, cm.make, COUNT(c) " +
           "FROM StorageLocation s " +
           "JOIN s.cars c " +
           "JOIN c.carModel cm " +
           "GROUP BY s.id, s.name, cm.make " +
           "ORDER BY s.name, COUNT(c) DESC")
    List<Object[]> getCarMakeCountByLocation();
    
    @Query("SELECT s.id, s.name, cm.bodyType, COUNT(c) " +
           "FROM StorageLocation s " +
           "JOIN s.cars c " +
           "JOIN c.carModel cm " +
           "WHERE cm.bodyType IS NOT NULL " +
           "GROUP BY s.id, s.name, cm.bodyType " +
           "ORDER BY s.name, COUNT(c) DESC")
    List<Object[]> getCarBodyTypeCountByLocation();
}