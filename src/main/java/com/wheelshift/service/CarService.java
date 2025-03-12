package com.wheelshift.service;

import com.wheelshift.dto.CarSearchCriteria;
import com.wheelshift.dto.CarStatistics;
import com.wheelshift.model.Car;
import com.wheelshift.model.CarDetailedSpecs;
import com.wheelshift.model.FinancialTransaction;
import com.wheelshift.model.Sale;
import com.wheelshift.model.StorageLocation;
import com.wheelshift.projection.CarBasicDetails;
import com.wheelshift.repository.CarDetailedSpecsRepository;
import com.wheelshift.repository.CarModelRepository;
import com.wheelshift.repository.CarRepository;
import com.wheelshift.repository.FinancialTransactionRepository;
import com.wheelshift.repository.StorageLocationRepository;
import com.wheelshift.repository.spec.CarSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarModelRepository carModelRepository;
    private final CarDetailedSpecsRepository carDetailedSpecsRepository;
    private final FinancialTransactionRepository financialTransactionRepository;
    private final StorageLocationRepository storageLocationRepository;
    
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

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }
    
    public Page<Car> getAllCars(Pageable pageable) {
        return carRepository.findAll(pageable);
    }
    
    public List<CarBasicDetails> getCarBasicDetails() {
        return carRepository.findAllCarBasicDetails();
    }
    
    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));
    }
    
    @Transactional
    public Car saveCar(Car car) {
        log.debug("Received request to save car: {}", car);

        // Validate required fields
        if (car.getVinNumber() == null || car.getVinNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("VIN number is required");
        }
        if (car.getYear() == null) {
            throw new IllegalArgumentException("Car year is required");
        }

        // Validate car model exists
        if (car.getCarModel() == null || car.getCarModel().getId() == null) {
            throw new IllegalArgumentException("Valid car model is required");
        }
        if (!carModelRepository.existsById(car.getCarModel().getId())) {
            throw new EntityNotFoundException("Car model not found with id: " + car.getCarModel().getId());
        }

        // Set default status if not provided
        if (car.getCurrentStatus() == null) {
            car.setCurrentStatus("Available");
        }

        log.debug("Validations completed. Proceeding with storage location handling...");

        // Handle storage location
        StorageLocation location = null;
        if (car.getStorageLocation() != null && car.getStorageLocation().getId() != null) {
            location = storageLocationRepository.findById(car.getStorageLocation().getId())
                .orElseThrow(() -> new EntityNotFoundException("Storage location not found with id: " + car.getStorageLocation().getId()));

            if (location.getTotalCapacity() != null &&
                location.getCurrentVehicleCount() >= location.getTotalCapacity()) {
                throw new IllegalStateException("Storage location is at full capacity");
            }
        }

        // Extract the specs before saving car to avoid circular reference
        CarDetailedSpecs specs = null;
        if (car.getDetailedSpecs() != null) {
            specs = car.getDetailedSpecs();
            car.setDetailedSpecs(null);  // Temporarily break the link
        }

        log.debug("Saving car entity...");
        Car savedCar = carRepository.save(car);
        log.debug("Car object saved in database with ID: {}", savedCar.getId());

        // Update storage location count and link
        if (location != null) {
            location.setCurrentVehicleCount(location.getCurrentVehicleCount() + 1);
            savedCar.setStorageLocation(location);
            storageLocationRepository.save(location);
        }

        // Now save the detailed specs with reference to the saved car
        if (specs != null) {
            specs.setCar(savedCar);
            log.debug("Saving car detailed specifications for Car ID: {}", savedCar.getId());
            CarDetailedSpecs savedSpecs = carDetailedSpecsRepository.save(specs);
            savedCar.setDetailedSpecs(savedSpecs);
            savedCar = carRepository.save(savedCar);
            log.debug("Car detailed specifications saved successfully with ID: {}", savedSpecs.getId());
        }

        // Handle Financial Transaction if Purchase Price is Provided
        if (savedCar.getPurchasePrice() != null && savedCar.getPurchaseDate() != null) {
            FinancialTransaction transaction = new FinancialTransaction();
            transaction.setCar(savedCar);
            transaction.setTransactionType("Purchase");
            transaction.setAmount(savedCar.getPurchasePrice());
            transaction.setTransactionDate(savedCar.getPurchaseDate());
            transaction.setDescription("Initial purchase of car: " + savedCar.getVinNumber());

            log.debug("Saving financial transaction for Car ID: {}", savedCar.getId());
            FinancialTransaction savedTransaction = financialTransactionRepository.save(transaction);

            if (savedCar.getFinancialTransactions() == null) {
                savedCar.setFinancialTransactions(new ArrayList<>());
            }
            savedCar.getFinancialTransactions().add(savedTransaction);
            savedCar = carRepository.save(savedCar);
            log.debug("Financial transaction saved successfully with ID: {}", savedTransaction.getId());
        }

        log.debug("Car save process completed successfully for ID: {}", savedCar.getId());
        return savedCar;
    }

    @Transactional
    public Car updateCar(Long id, Car carDetails) {
        // Get existing car or throw exception if not found
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));
        
        // Validate car model if provided
        if (carDetails.getCarModel() != null && carDetails.getCarModel().getId() != null) {
            if (!carModelRepository.existsById(carDetails.getCarModel().getId())) {
                throw new EntityNotFoundException("Car model not found with id: " + carDetails.getCarModel().getId());
            }
            existingCar.setCarModel(carDetails.getCarModel());
        }
        
        // Handle storage location change
        handleLocationChange(existingCar, carDetails);
        
        // Update basic properties if provided
        if (carDetails.getVinNumber() != null) {
            existingCar.setVinNumber(carDetails.getVinNumber());
        }
        
        if (carDetails.getRegistrationNumber() != null) {
            existingCar.setRegistrationNumber(carDetails.getRegistrationNumber());
        }
        
        if (carDetails.getYear() != null) {
            existingCar.setYear(carDetails.getYear());
        }
        
        if (carDetails.getColor() != null) {
            existingCar.setColor(carDetails.getColor());
        }
        
        if (carDetails.getMileage() != null) {
            existingCar.setMileage(carDetails.getMileage());
        }
        
        if (carDetails.getEngineCapacity() != null) {
            existingCar.setEngineCapacity(carDetails.getEngineCapacity());
        }
        
        if (carDetails.getCurrentStatus() != null) {
            existingCar.setCurrentStatus(carDetails.getCurrentStatus());
        }
        
        if (carDetails.getPurchaseDate() != null) {
            existingCar.setPurchaseDate(carDetails.getPurchaseDate());
        }
        
        if (carDetails.getPurchasePrice() != null) {
            existingCar.setPurchasePrice(carDetails.getPurchasePrice());
        }
        
        if (carDetails.getSellingPrice() != null) {
            existingCar.setSellingPrice(carDetails.getSellingPrice());
        }
        
        // Handle detailed specs if provided
        if (carDetails.getDetailedSpecs() != null) {
            updateCarDetailedSpecs(existingCar.getId(), carDetails.getDetailedSpecs());
        }
        
        // Save and return the updated car
        return carRepository.save(existingCar);
    }

    @Transactional
    public void deleteCar(Long id) {
        Car car = getCarById(id);
        
        // Update location vehicle count if applicable
        if (car.getStorageLocation() != null) {
            StorageLocation location = car.getStorageLocation();
            location.setCurrentVehicleCount(location.getCurrentVehicleCount() - 1);
            storageLocationRepository.save(location);
        }
        
        carRepository.deleteById(id);
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
    
    @Transactional
    public Car addFinancialTransaction(Long carId, FinancialTransaction transaction) {
        Car car = getCarById(carId);
        transaction.setCar(car);
        car.getFinancialTransactions().add(transaction);
        return carRepository.save(car);
    }
    
    @Transactional
    public Car updateSale(Long carId, Sale sale) {
        Car car = getCarById(carId);
        
        // If car already has a sale, remove it
        if (car.getSale() != null) { car.getSale().setCar(null); }
        
        // Set new sale
        sale.setCar(car); car.setSale(sale);
        
        // Update car status to "Sold"
        car.setCurrentStatus("Sold");
        return carRepository.save(car);
    }
    
    @Transactional
    public Car changeStatus(Long carId, String newStatus) {
        Car car = getCarById(carId);
        car.setCurrentStatus(newStatus);
        return carRepository.save(car);
    }
    
    @Transactional
    public Car moveToLocation(Long carId, Long locationId) {
        Car car = getCarById(carId);
        StorageLocation oldLocation = car.getStorageLocation();
        StorageLocation newLocation = storageLocationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + locationId));
        
        // Update old location count
        if (oldLocation != null) {
            oldLocation.setCurrentVehicleCount(oldLocation.getCurrentVehicleCount() - 1);
            storageLocationRepository.save(oldLocation);
        }
        
        // Update new location count
        newLocation.setCurrentVehicleCount(newLocation.getCurrentVehicleCount() + 1);
        storageLocationRepository.save(newLocation);
        
        // Update car location
        car.setStorageLocation(newLocation);
        
        return carRepository.save(car);
    }
    
    @Transactional
    public Car updateCarDetailedSpecs(Long carId, CarDetailedSpecs detailedSpecs) {
        Car car = getCarById(carId);
        
        // If car already has detailed specs, update it
        if (car.getDetailedSpecs() != null) {
            CarDetailedSpecs existingSpecs = car.getDetailedSpecs();
            
            // Update fields if provided
            if (detailedSpecs.getDoors() != null) {
                existingSpecs.setDoors(detailedSpecs.getDoors());
            }
            
            if (detailedSpecs.getSeats() != null) {
                existingSpecs.setSeats(detailedSpecs.getSeats());
            }
            
            if (detailedSpecs.getCargoCapacityLiters() != null) {
                existingSpecs.setCargoCapacityLiters(detailedSpecs.getCargoCapacityLiters());
            }
            
            if (detailedSpecs.getAcceleration0To100() != null) {
                existingSpecs.setAcceleration0To100(detailedSpecs.getAcceleration0To100());
            }
            
            if (detailedSpecs.getTopSpeed() != null) {
                existingSpecs.setTopSpeed(detailedSpecs.getTopSpeed());
            }
            
            // Update additional features if provided
            if (detailedSpecs.getAdditionalFeatures() != null && !detailedSpecs.getAdditionalFeatures().isEmpty()) {
                if (existingSpecs.getAdditionalFeatures() == null) {
                    existingSpecs.setAdditionalFeatures(new HashMap<>());
                }
                existingSpecs.getAdditionalFeatures().putAll(detailedSpecs.getAdditionalFeatures());
            }
            
            carDetailedSpecsRepository.save(existingSpecs);
        } else {
        	// Create new detailed specs - break the link first to avoid circular reference
            car.setDetailedSpecs(null);
            car = carRepository.save(car);  // Save to ensure car exists in DB
            
            detailedSpecs.setCar(car);
            CarDetailedSpecs savedSpecs = carDetailedSpecsRepository.save(detailedSpecs);
            car.setDetailedSpecs(savedSpecs);
        }
        
        return carRepository.save(car);
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
    
    public Page<Car> searchCars(String searchTerm, Pageable pageable) {
        return carRepository.searchCars(searchTerm, pageable);
    }
    
    public Page<Car> searchCarsAdvanced(CarSearchCriteria criteria, Pageable pageable) {
        Specification<Car> spec = Specification.where(null);
        
        if (criteria.getMake() != null) {
            spec = spec.and(CarSpecification.hasMake(criteria.getMake()));
        }
        
        if (criteria.getModel() != null) {
            spec = spec.and(CarSpecification.hasModel(criteria.getModel()));
        }
        
        if (criteria.getBodyType() != null) {
            spec = spec.and(CarSpecification.hasBodyType(criteria.getBodyType()));
        }
        
        if (criteria.getFuelType() != null) {
            spec = spec.and(CarSpecification.hasFuelType(criteria.getFuelType()));
        }
        
        if (criteria.getTransmissionType() != null) {
            spec = spec.and(CarSpecification.hasTransmissionType(criteria.getTransmissionType()));
        }
        
        if (criteria.getColor() != null) {
            spec = spec.and(CarSpecification.hasColor(criteria.getColor()));
        }
        
        if (criteria.getYearFrom() != null || criteria.getYearTo() != null) {
            spec = spec.and(CarSpecification.hasYearBetween(criteria.getYearFrom(), criteria.getYearTo()));
        }
        
        if (criteria.getMinPrice() != null || criteria.getMaxPrice() != null) {
            spec = spec.and(CarSpecification.hasPriceBetween(criteria.getMinPrice(), criteria.getMaxPrice()));
        }
        
        if (criteria.getMinMileage() != null || criteria.getMaxMileage() != null) {
            spec = spec.and(CarSpecification.hasMileageBetween(criteria.getMinMileage(), criteria.getMaxMileage()));
        }
        
        if (criteria.getStatus() != null) {
            spec = spec.and(CarSpecification.hasStatus(criteria.getStatus()));
        }
        
        if (criteria.getLocationId() != null) {
            spec = spec.and(CarSpecification.hasLocationId(criteria.getLocationId()));
        }
        
        if (criteria.getPurchaseDateFrom() != null || criteria.getPurchaseDateTo() != null) {
            spec = spec.and(CarSpecification.purchasedBetween(criteria.getPurchaseDateFrom(), criteria.getPurchaseDateTo()));
        }
        
        if (criteria.getSearchText() != null && !criteria.getSearchText().trim().isEmpty()) {
            spec = spec.and(CarSpecification.containsText(criteria.getSearchText()));
        }
        
        return carRepository.findAll(spec, pageable);
    }
    
    public Optional<Car> findByVinNumber(String vinNumber) {
        return carRepository.findByVinNumber(vinNumber);
    }
    
    public Optional<Car> findByRegistrationNumber(String registrationNumber) {
        return carRepository.findByRegistrationNumber(registrationNumber);
    }
    
    public List<Car> findByMake(String make) {
        return carRepository.findByCarModel_Make(make);
    }
    
    public List<Car> findByModel(String model) {
        return carRepository.findByCarModel_Model(model);
    }
    
    public List<Car> findByMakeAndModel(String make, String model) {
        return carRepository.findByCarModel_MakeAndCarModel_Model(make, model);
    }
    
    public List<Car> findByStatus(String status) {
        return carRepository.findByCurrentStatus(status);
    }
    
    public List<Car> findByYear(Integer year) {
        return carRepository.findByYear(year);
    }
    
    public List<Car> findByColor(String color) {
        return carRepository.findByColor(color);
    }
    
    public List<Car> findByFuelType(String fuelType) {
        return carRepository.findByCarModel_FuelType(fuelType);
    }
    
    public List<Car> findByBodyType(String bodyType) {
        return carRepository.findByCarModel_BodyType(bodyType);
    }
    
    public List<Car> findByTransmissionType(String transmissionType) {
        return carRepository.findByCarModel_TransmissionType(transmissionType);
    }
    
    public List<Car> findByLocationId(Long locationId) {
        return carRepository.findByStorageLocation_Id(locationId);
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
    
    public CarStatistics getCarStatistics() {
        CarStatistics statistics = new CarStatistics();
        
        // Basic counts
        statistics.setTotalCars(carRepository.count());
        statistics.setAvailableCars(carRepository.countByStatus("available"));
        statistics.setReservedCars(carRepository.countByStatus("reserved"));
        statistics.setSoldCars(carRepository.countByStatus("sold"));
        
        // Financial statistics
        statistics.setAverageProfitMargin(carRepository.getAverageProfitMargin());
        statistics.setAverageMileage(carRepository.getAverageMileageOfAvailableCars());
        statistics.setAverageDaysToSell(carRepository.getAverageDaysToSell());
        
        // Inventory by make
        Map<String, Long> inventoryByMake = carRepository.getInventoryByMake()
                .stream()
                .collect(Collectors.toMap(
                    obj -> (String) obj[0],  // make
                    obj -> (Long) obj[1]     // count
                ));
        statistics.setInventoryByMake(inventoryByMake);
        
        // Inventory by body type
        Map<String, Long> inventoryByBodyType = carRepository.getInventoryByBodyType()
                .stream()
                .collect(Collectors.toMap(
                    obj -> (String) obj[0],  // body type
                    obj -> (Long) obj[1]     // count
                ));
        statistics.setInventoryByBodyType(inventoryByBodyType);
        
        // Purchase count by year
        Map<Integer, Long> purchaseCountByYear = carRepository.getCarPurchaseCountByYear()
                .stream()
                .collect(Collectors.toMap(
                    obj -> (Integer) obj[0],  // year
                    obj -> (Long) obj[1]      // count
                ));
        statistics.setPurchaseCountByYear(purchaseCountByYear);
        
        // For current year's purchase count by month
        if (!purchaseCountByYear.isEmpty()) {
            Integer currentYear = purchaseCountByYear.keySet().stream()
                    .max(Integer::compareTo)
                    .orElse(java.time.LocalDate.now().getYear());
            
            Map<Integer, Long> purchaseCountByMonth = carRepository.getCarPurchaseCountByMonth(currentYear)
                    .stream()
                    .collect(Collectors.toMap(
                        obj -> (Integer) obj[0],  // month
                        obj -> (Long) obj[1]      // count
                    ));
            statistics.setPurchaseCountByMonth(purchaseCountByMonth);
        } else {
            statistics.setPurchaseCountByMonth(new HashMap<>());
        }
        
        // Average mileage by model
        Map<String, BigDecimal> avgMileageByModel = carRepository.getAverageMileageByModel()
                .stream()
                .collect(Collectors.toMap(
                    obj -> obj[0] + " " + obj[1],  // make + model
                    obj -> (BigDecimal) obj[2]     // avg mileage
                ));
        statistics.setAverageMileageByModel(avgMileageByModel);
        
        // Average days to sell by make
        Map<String, Double> avgDaysToSellByMake = carRepository.getAverageDaysToSellByMake()
                .stream()
                .collect(Collectors.toMap(
                    obj -> (String) obj[0],    // make
                    obj -> (Double) obj[1]     // avg days
                ));
        statistics.setAverageDaysToSellByMake(avgDaysToSellByMake);
        
        return statistics;
    }
    
    /**
     *    _    _ ______ _      _____  ______ _____  
	 *	 | |  | |  ____| |    |  __ \|  ____|  __ \ 
	 *	 | |__| | |__  | |    | |__) | |__  | |__) |
	 *	 |  __  |  __| | |    |  ___/|  __| |  _  / 
	 *	 | |  | | |____| |____| |    | |____| | \ \ 
	 *	 |_|  |_|______|______|_|    |______|_|  \_\
     *				
     *				HELPER FUNCTIONS                         
     */
    
    private void handleLocationChange(Car existingCar, Car carDetails) {
        // Check if location is provided and has changed
        if (carDetails.getStorageLocation() != null && carDetails.getStorageLocation().getId() != null) {
            Long newLocationId = carDetails.getStorageLocation().getId();
            boolean locationChanged = existingCar.getStorageLocation() == null || 
                                     !existingCar.getStorageLocation().getId().equals(newLocationId);
            
            if (locationChanged) {
                // Decrement count in old location if it exists
                if (existingCar.getStorageLocation() != null) {
                    StorageLocation oldLocation = existingCar.getStorageLocation();
                    oldLocation.setCurrentVehicleCount(Math.max(0, oldLocation.getCurrentVehicleCount() - 1));
                    storageLocationRepository.save(oldLocation);
                }
                
                // Get and update new location
                StorageLocation newLocation = storageLocationRepository.findById(newLocationId)
                    .orElseThrow(() -> new EntityNotFoundException("Storage location not found with id: " + newLocationId));
                
                newLocation.setCurrentVehicleCount(newLocation.getCurrentVehicleCount() + 1);
                existingCar.setStorageLocation(newLocation);
                storageLocationRepository.save(newLocation);
            }
        } else if (carDetails.getStorageLocation() == null && existingCar.getStorageLocation() != null) {
            // If new location is null but old location exists, decrement count in old location
            StorageLocation oldLocation = existingCar.getStorageLocation();
            oldLocation.setCurrentVehicleCount(Math.max(0, oldLocation.getCurrentVehicleCount() - 1));
            storageLocationRepository.save(oldLocation);
            existingCar.setStorageLocation(null);
        }
    }
    
}