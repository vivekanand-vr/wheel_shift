package com.wheelshift.service;

import com.wheelshift.model.Car;
import com.wheelshift.model.CarInspection;
import com.wheelshift.repository.CarInspectionRepository;
import com.wheelshift.repository.CarRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarInspectionService {
    
    private final CarInspectionRepository carInspectionRepository;
    private final CarRepository carRepository;
    
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

    public List<CarInspection> getAllInspections() {
        return carInspectionRepository.findAll();
    }
    
    public Optional<CarInspection> getInspectionById(Long id) {
        return carInspectionRepository.findById(id);
    }
    
    @Transactional
    public CarInspection saveInspection(CarInspection inspection) {
        // Check if this car requires updates based on inspection results
        if (!inspection.getInspectionPass()) {
            // If inspection failed, update the car status
            Car car = inspection.getCar();
            car.setCurrentStatus("NEEDS_REPAIR");
            carRepository.save(car);
        }
        
        return carInspectionRepository.save(inspection);
    }
    
    @Transactional
    public CarInspection updateInspection(Long id, CarInspection updatedInspection) {
        return carInspectionRepository.findById(id)
                .map(inspection -> {
                    inspection.setInspectionDate(updatedInspection.getInspectionDate());
                    inspection.setInspectorName(updatedInspection.getInspectorName());
                    inspection.setOverallCondition(updatedInspection.getOverallCondition());
                    inspection.setExteriorCondition(updatedInspection.getExteriorCondition());
                    inspection.setInteriorCondition(updatedInspection.getInteriorCondition());
                    inspection.setMechanicalCondition(updatedInspection.getMechanicalCondition());
                    inspection.setElectricalCondition(updatedInspection.getElectricalCondition());
                    inspection.setAccidentHistory(updatedInspection.getAccidentHistory());
                    inspection.setRequiredRepairs(updatedInspection.getRequiredRepairs());
                    inspection.setEstimatedRepairCost(updatedInspection.getEstimatedRepairCost());
                    inspection.setInspectionPass(updatedInspection.getInspectionPass());
                    inspection.setInspectionReportPdf(updatedInspection.getInspectionReportPdf());
                    inspection.setInspectionReportFilename(updatedInspection.getInspectionReportFilename());
                    
                    // Update car status if inspection pass status changes
                    if (!inspection.getInspectionPass()) {
                        Car car = inspection.getCar();
                        car.setCurrentStatus("NEEDS_REPAIR");
                        carRepository.save(car);
                    }
                    
                    return carInspectionRepository.save(inspection);
                })
                .orElse(null);
    }
    
    @Transactional
    public void deleteInspection(Long id) {
        carInspectionRepository.deleteById(id);
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
    
    public List<CarInspection> getInspectionsByCar(Long carId) {
        Optional<Car> car = carRepository.findById(carId);
        return car.map(carInspectionRepository::findByCar).orElse(List.of());
    }
    
    public List<CarInspection> getInspectionsByVin(String vinNumber) {
        return carInspectionRepository.findByCarVinNumber(vinNumber);
    }
    
    public List<CarInspection> getInspectionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return carInspectionRepository.findByInspectionDateBetween(startDate, endDate);
    }
    
    public List<CarInspection> getInspectionsByInspector(String inspectorName) {
        return carInspectionRepository.findByInspectorNameContainingIgnoreCase(inspectorName);
    }
    
    public List<CarInspection> getInspectionsByPassStatus(Boolean pass) {
        return carInspectionRepository.findByInspectionPass(pass);
    }

    public List<CarInspection> getLatestInspectionsByCar(Long carId) {
        return carInspectionRepository.findLatestInspectionsByCar(carId);
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
    
    public Map<String, Object> getInspectionStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        Long passedCount = carInspectionRepository.countPassedInspections();
        Long failedCount = carInspectionRepository.countFailedInspections();
        
        statistics.put("passedCount", passedCount);
        statistics.put("failedCount", failedCount);
        statistics.put("passRate", calculatePassRate(passedCount, failedCount));
        
        return statistics;
    }
    
    /**
	 *	  ____  _    _  _____ _____ _   _ ______  _____ _____   _      ____   _____ _____ _____ 
	 *	 |  _ \| |  | |/ ____|_   _| \ | |  ____|/ ____/ ____| | |    / __ \ / ____|_   _/ ____|
	 *	 | |_) | |  | | (___   | | |  \| | |__  | (___| (___   | |   | |  | | |  __  | || |     
	 *	 |  _ <| |  | |\___ \  | | | . ` |  __|  \___ \\___ \  | |   | |  | | | |_ | | || |     
	 *	 | |_) | |__| |____) |_| |_| |\  | |____ ____) |___) | | |___| |__| | |__| |_| || |____ 
	 * 	 |____/ \____/|_____/|_____|_| \_|______|_____/_____/  |______\____/ \_____|_____\_____|
     *                                                                                   
     *				BUSINESS LOGIC & TRANSACTIONS                                                                                   
     */
    
    public BigDecimal getTotalEstimatedRepairCosts() {
        List<CarInspection> failedInspections = carInspectionRepository.findByInspectionPass(false);
        return failedInspections.stream()
                .map(CarInspection::getEstimatedRepairCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private double calculatePassRate(Long passed, Long failed) {
        long total = passed + failed;
        if (total == 0) {
            return 0.0;
        }
        return (double) passed / total * 100;
    }
}