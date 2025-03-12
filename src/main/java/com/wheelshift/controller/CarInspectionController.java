package com.wheelshift.controller;

import com.wheelshift.model.CarInspection;
import com.wheelshift.service.CarInspectionService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inspections")
@RequiredArgsConstructor
public class CarInspectionController {

    private final CarInspectionService carInspectionService;
    
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
    public ResponseEntity<List<CarInspection>> getAllInspections() {
        return ResponseEntity.ok(carInspectionService.getAllInspections());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CarInspection> getInspectionById(@PathVariable Long id) {
        return carInspectionService.getInspectionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<CarInspection> createInspection(@RequestBody CarInspection inspection) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(carInspectionService.saveInspection(inspection));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CarInspection> updateInspection(
            @PathVariable Long id, 
            @RequestBody CarInspection inspection) {
        CarInspection updatedInspection = carInspectionService.updateInspection(id, inspection);
        return updatedInspection != null 
                ? ResponseEntity.ok(updatedInspection) 
                : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInspection(@PathVariable Long id) {
        carInspectionService.deleteInspection(id);
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
    public ResponseEntity<List<CarInspection>> getInspectionsByCar(@PathVariable Long carId) {
        return ResponseEntity.ok(carInspectionService.getInspectionsByCar(carId));
    }
    
    @GetMapping("/vin/{vinNumber}")
    public ResponseEntity<List<CarInspection>> getInspectionsByVin(@PathVariable String vinNumber) {
        return ResponseEntity.ok(carInspectionService.getInspectionsByVin(vinNumber));
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<CarInspection>> getInspectionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(carInspectionService.getInspectionsByDateRange(startDate, endDate));
    }
    
    @GetMapping("/inspector/{inspectorName}")
    public ResponseEntity<List<CarInspection>> getInspectionsByInspector(@PathVariable String inspectorName) {
        return ResponseEntity.ok(carInspectionService.getInspectionsByInspector(inspectorName));
    }
    
    @GetMapping("/pass-status")
    public ResponseEntity<List<CarInspection>> getInspectionsByPassStatus(@RequestParam Boolean pass) {
        return ResponseEntity.ok(carInspectionService.getInspectionsByPassStatus(pass));
    }
    
    @GetMapping("/car/{carId}/latest")
    public ResponseEntity<List<CarInspection>> getLatestInspectionsByCar(@PathVariable Long carId) {
        return ResponseEntity.ok(carInspectionService.getLatestInspectionsByCar(carId));
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
    public ResponseEntity<Map<String, Object>> getInspectionStatistics() {
        return ResponseEntity.ok(carInspectionService.getInspectionStatistics());
    }
    
    @GetMapping("/total-repair-costs")
    public ResponseEntity<java.math.BigDecimal> getTotalEstimatedRepairCosts() {
        return ResponseEntity.ok(carInspectionService.getTotalEstimatedRepairCosts());
    }
}