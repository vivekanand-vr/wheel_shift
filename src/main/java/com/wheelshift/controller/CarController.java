package com.wheelshift.controller;

import com.wheelshift.dto.CarSearchCriteria;
import com.wheelshift.dto.CarStatistics;
import com.wheelshift.model.Car;
import com.wheelshift.model.FinancialTransaction;
import com.wheelshift.model.Sale;
import com.wheelshift.projection.CarBasicDetails;
import com.wheelshift.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

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
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }
    
    @GetMapping("/basic-details")
    public ResponseEntity<List<CarBasicDetails>> getCarBasicDetails() {
        return ResponseEntity.ok(carService.getCarBasicDetails());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<Car>> getAllCarsPaged(Pageable pageable) {
        return ResponseEntity.ok(carService.getAllCars(pageable));
    }
    
    @GetMapping("/basic-details/paged")
    public ResponseEntity<Page<CarBasicDetails>> getCarBasicDetailsPaged(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(carService.getCarBasicDetailsPaged(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PostMapping
    public ResponseEntity<Car> createCar(@Valid @RequestBody Car car) {
        return new ResponseEntity<>(carService.saveCar(car), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @Valid @RequestBody Car car) {
        return ResponseEntity.ok(carService.updateCar(id, car));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
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
     
    @PostMapping("/{id}/transactions")
    public ResponseEntity<Car> addFinancialTransaction(
            @PathVariable Long id, 
            @Valid @RequestBody FinancialTransaction transaction) {
        return ResponseEntity.ok(carService.addFinancialTransaction(id, transaction));
    }

    @PostMapping("/{id}/sale")
    public ResponseEntity<Car> updateSale(
            @PathVariable Long id, 
            @Valid @RequestBody Sale sale) {
        return ResponseEntity.ok(carService.updateSale(id, sale));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Car> changeStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        return ResponseEntity.ok(carService.changeStatus(id, status));
    }

    @PatchMapping("/{id}/location/{locationId}")
    public ResponseEntity<Car> moveToLocation(
            @PathVariable Long id, 
            @PathVariable Long locationId) {
        return ResponseEntity.ok(carService.moveToLocation(id, locationId));
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

    @GetMapping("/search")
    public ResponseEntity<Page<Car>> searchCars(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(carService.searchCars(searchTerm, pageable));
    }

    @PostMapping("/search/advanced")
    public ResponseEntity<Page<Car>> searchCarsAdvanced(
            @RequestBody CarSearchCriteria criteria, 
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)  {
    	Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(carService.searchCarsAdvanced(criteria, pageable));
    }
    
    @GetMapping("/vin/{vinNumber}")
    public ResponseEntity<Car> findByVinNumber(@PathVariable String vinNumber) {
        Optional<Car> car = carService.findByVinNumber(vinNumber);
        return car.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/registration/{registrationNumber}")
    public ResponseEntity<Car> findByRegistrationNumber(@PathVariable String registrationNumber) {
        Optional<Car> car = carService.findByRegistrationNumber(registrationNumber);
        return car.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/make/{make}")
    public ResponseEntity<List<Car>> findByMake(@PathVariable String make) {
        return ResponseEntity.ok(carService.findByMake(make));
    }

    @GetMapping("/model/{model}")
    public ResponseEntity<List<Car>> findByModel(@PathVariable String model) {
        return ResponseEntity.ok(carService.findByModel(model));
    }

    @GetMapping("/make/{make}/model/{model}")
    public ResponseEntity<List<Car>> findByMakeAndModel(
            @PathVariable String make, 
            @PathVariable String model) {
        return ResponseEntity.ok(carService.findByMakeAndModel(make, model));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Car>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(carService.findByStatus(status));
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<Car>> findByYear(@PathVariable Integer year) {
        return ResponseEntity.ok(carService.findByYear(year));
    }

    @GetMapping("/color/{color}")
    public ResponseEntity<List<Car>> findByColor(@PathVariable String color) {
        return ResponseEntity.ok(carService.findByColor(color));
    }

    @GetMapping("/fuel-type/{fuelType}")
    public ResponseEntity<List<Car>> findByFuelType(@PathVariable String fuelType) {
        return ResponseEntity.ok(carService.findByFuelType(fuelType));
    }

    @GetMapping("/body-type/{bodyType}")
    public ResponseEntity<List<Car>> findByBodyType(@PathVariable String bodyType) {
        return ResponseEntity.ok(carService.findByBodyType(bodyType));
    }

    @GetMapping("/transmission/{transmissionType}")
    public ResponseEntity<List<Car>> findByTransmissionType(@PathVariable String transmissionType) {
        return ResponseEntity.ok(carService.findByTransmissionType(transmissionType));
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<Car>> findByLocationId(@PathVariable Long locationId) {
        return ResponseEntity.ok(carService.findByLocationId(locationId));
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
    public ResponseEntity<CarStatistics> getCarStatistics() {
        return ResponseEntity.ok(carService.getCarStatistics());
    }
}