package com.wheelshift.repository;

import com.wheelshift.model.Car;
import com.wheelshift.model.CarInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CarInspectionRepository extends JpaRepository<CarInspection, Long> {
    
    List<CarInspection> findByCar(Car car);
    
    List<CarInspection> findByCarVinNumber(String vinNumber);
    
    List<CarInspection> findByInspectionDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<CarInspection> findByInspectorNameContainingIgnoreCase(String inspectorName);
    
    List<CarInspection> findByInspectionPass(Boolean pass);
    
    @Query("SELECT i FROM CarInspection i WHERE i.car.id = :carId ORDER BY i.inspectionDate DESC")
    List<CarInspection> findLatestInspectionsByCar(Long carId);
    
    @Query("SELECT COUNT(i) FROM CarInspection i WHERE i.inspectionPass = true")
    Long countPassedInspections();
    
    @Query("SELECT COUNT(i) FROM CarInspection i WHERE i.inspectionPass = false")
    Long countFailedInspections();
}