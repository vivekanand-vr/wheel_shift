package com.wheelshift.service;

import com.wheelshift.dto.LocationStatistics;
import com.wheelshift.model.StorageLocation;
import com.wheelshift.repository.StorageLocationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StorageLocationService {

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

    public List<StorageLocation> getAllLocations() {
        return storageLocationRepository.findAll();
    }
    
    public StorageLocation getLocationById(Long id) {
        return storageLocationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + id));
    }
    
    public StorageLocation saveLocation(StorageLocation location) {
        // Initialize current vehicle count if not set
        if (location.getCurrentVehicleCount() == null) {
            location.setCurrentVehicleCount(0);
        }
        
        return storageLocationRepository.save(location);
    }
    
    public StorageLocation updateLocation(Long id, StorageLocation locationDetails) {
        StorageLocation location = getLocationById(id);
        
        location.setName(locationDetails.getName());
        location.setAddress(locationDetails.getAddress());
        location.setContactPerson(locationDetails.getContactPerson());
        location.setContactNumber(locationDetails.getContactNumber());
        location.setTotalCapacity(locationDetails.getTotalCapacity());
        
        // Don't update currentVehicleCount directly through this method
        // as it's managed by the car service
        
        return storageLocationRepository.save(location);
    }
    
    @Transactional
    public void deleteLocation(Long id) {
        StorageLocation location = getLocationById(id);
        
        // Check if location has cars
        if (location.getCurrentVehicleCount() > 0) {
            throw new IllegalStateException("Cannot delete location with cars. Move cars to another location first.");
        }
        
        storageLocationRepository.deleteById(id);
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
    public StorageLocation updateVehicleCount(Long id, int delta) {
        StorageLocation location = getLocationById(id);
        
        int newCount = location.getCurrentVehicleCount() + delta;
        
        // Validate the new count
        if (newCount < 0) {
            throw new IllegalArgumentException("Vehicle count cannot be negative");
        }
        
        if (location.getTotalCapacity() != null && newCount > location.getTotalCapacity()) {
            throw new IllegalArgumentException("Location capacity exceeded");
        }
        
        location.setCurrentVehicleCount(newCount);
        return storageLocationRepository.save(location);
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
    
    public List<StorageLocation> findLocationsWithAvailableSpace() {
        return storageLocationRepository.findLocationsWithAvailableCapacity();
    }
    
    public List<StorageLocation> findLocationsWithMinimumSpace(Integer requiredSpace) {
        return storageLocationRepository.findLocationsWithCapacityAtLeast(requiredSpace);
    }
    
    public boolean hasAvailableCapacity(Long id, int requiredSpace) {
        StorageLocation location = getLocationById(id);
        
        if (location.getTotalCapacity() == null) {
            return true; // No capacity limit set
        }
        
        int availableSpace = location.getTotalCapacity() - location.getCurrentVehicleCount();
        return availableSpace >= requiredSpace;
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
    
    public LocationStatistics getLocationStatistics() {
        LocationStatistics statistics = new LocationStatistics();
        
        // Basic stats
        statistics.setTotalLocations(storageLocationRepository.count());
        statistics.setEmptyLocations(storageLocationRepository.countEmptyLocations());
        statistics.setFullLocations(storageLocationRepository.countFullLocations());
        statistics.setTotalCapacity(storageLocationRepository.getTotalCapacity());
        statistics.setTotalVehiclesStored(storageLocationRepository.getTotalVehiclesStored());
        statistics.setAverageOccupancyPercentage(storageLocationRepository.getAverageOccupancyPercentage());
        
        // Occupancy rates by location
        List<Object[]> occupancyRates = storageLocationRepository.getLocationOccupancyRates();
        Map<String, Double> occupancyByLocation = occupancyRates.stream()
                .collect(Collectors.toMap(
                    obj -> (String) obj[0],  // location name
                    obj -> (Double) obj[3]   // occupancy percentage
                ));
        statistics.setOccupancyByLocation(occupancyByLocation);
        
        // Car makes by location
        List<Object[]> carMakesByLocation = storageLocationRepository.getCarMakeCountByLocation();
        Map<String, Map<String, Long>> makesByLocation = new HashMap<>();
        
        for (Object[] result : carMakesByLocation) {
            String locationName = (String) result[1];
            String make = (String) result[2];
            Long count = (Long) result[3];
            
            makesByLocation.computeIfAbsent(locationName, k -> new HashMap<>())
                          .put(make, count);
        }
        statistics.setCarMakesByLocation(makesByLocation);
        
        // Car body types by location
        List<Object[]> carBodyTypesByLocation = storageLocationRepository.getCarBodyTypeCountByLocation();
        Map<String, Map<String, Long>> bodyTypesByLocation = new HashMap<>();
        
        for (Object[] result : carBodyTypesByLocation) {
            String locationName = (String) result[1];
            String bodyType = (String) result[2];
            Long count = (Long) result[3];
            
            bodyTypesByLocation.computeIfAbsent(locationName, k -> new HashMap<>())
                              .put(bodyType, count);
        }
        statistics.setCarBodyTypesByLocation(bodyTypesByLocation);
        
        return statistics;
    }
}