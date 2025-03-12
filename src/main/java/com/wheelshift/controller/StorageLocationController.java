package com.wheelshift.controller;

import com.wheelshift.dto.LocationStatistics;
import com.wheelshift.model.StorageLocation;
import com.wheelshift.service.StorageLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class StorageLocationController {

    private final StorageLocationService storageLocationService;

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
    public ResponseEntity<List<StorageLocation>> getAllLocations() {
        return ResponseEntity.ok(storageLocationService.getAllLocations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorageLocation> getLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(storageLocationService.getLocationById(id));
    }

    @PostMapping
    public ResponseEntity<StorageLocation> createLocation(@Valid @RequestBody StorageLocation location) {
        return new ResponseEntity<>(storageLocationService.saveLocation(location), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StorageLocation> updateLocation(
            @PathVariable Long id, 
            @Valid @RequestBody StorageLocation location) {
        return ResponseEntity.ok(storageLocationService.updateLocation(id, location));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        storageLocationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
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
     
    @PatchMapping("/{id}/vehicle-count")
    public ResponseEntity<StorageLocation> updateVehicleCount(
            @PathVariable Long id, 
            @RequestParam int delta) {
        return ResponseEntity.ok(storageLocationService.updateVehicleCount(id, delta));
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
       
    @GetMapping("/available")
    public ResponseEntity<List<StorageLocation>> getLocationsWithAvailableSpace() {
        return ResponseEntity.ok(storageLocationService.findLocationsWithAvailableSpace());
    }

    @GetMapping("/available/{requiredSpace}")
    public ResponseEntity<List<StorageLocation>> getLocationsWithMinimumSpace(
            @PathVariable Integer requiredSpace) {
        return ResponseEntity.ok(storageLocationService.findLocationsWithMinimumSpace(requiredSpace));
    }

    @GetMapping("/{id}/has-capacity")
    public ResponseEntity<Boolean> checkAvailableCapacity(
            @PathVariable Long id, 
            @RequestParam int requiredSpace) {
        return ResponseEntity.ok(storageLocationService.hasAvailableCapacity(id, requiredSpace));
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
    public ResponseEntity<LocationStatistics> getLocationStatistics() {
        return ResponseEntity.ok(storageLocationService.getLocationStatistics());
    }
}