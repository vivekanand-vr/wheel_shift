package com.wheelshift.controller;

import com.wheelshift.model.CarModel;
import com.wheelshift.service.CarModelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/car-models")
public class CarModelController {

    private final CarModelService carModelService;

    public CarModelController(CarModelService carModelService) {
        this.carModelService = carModelService;
    }
    
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
    public ResponseEntity<List<CarModel>> getAllCarModels() {
        return ResponseEntity.ok(carModelService.findAllCarModels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarModel> getCarModelById(@PathVariable Integer id) {
        return carModelService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<CarModel> createCarModel(@RequestBody CarModel carModel) {
        CarModel savedModel = carModelService.saveCarModel(carModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarModel> updateCarModel(
            @PathVariable Integer id,
            @RequestBody CarModel carModel) {
        if (!carModelService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        carModel.setId(id);
        return ResponseEntity.ok(carModelService.updateCarModel(carModel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarModel(@PathVariable Integer id) {
        if (!carModelService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        carModelService.deleteCarModel(id);
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
       
    @GetMapping("/search")
    public ResponseEntity<List<CarModel>> searchByMakeAndModel(
            @RequestParam String make,
            @RequestParam String model) {
        return carModelService.findByMakeAndModel(make, model)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/variant")
    public ResponseEntity<CarModel> searchByMakeModelAndVariant(
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam String variant) {
        return carModelService.findByMakeAndModelAndVariant(make, model, variant)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/makes")
    public ResponseEntity<List<String>> getAllMakes() {
        return ResponseEntity.ok(carModelService.getAllMakes());
    }

    @GetMapping("/makes/{make}/models")
    public ResponseEntity<List<String>> getModelsByMake(@PathVariable String make) {
        return ResponseEntity.ok(carModelService.getModelNamesByMake(make));
    }

    @GetMapping("/makes/{make}/all-models")
    public ResponseEntity<List<CarModel>> getAllModelsByMake(@PathVariable String make) {
        return ResponseEntity.ok(carModelService.getModelsByMake(make));
    }

    @GetMapping("/makes/{make}/models/{model}/variants")
    public ResponseEntity<List<String>> getVariantsByMakeAndModel(
            @PathVariable String make,
            @PathVariable String model) {
        return ResponseEntity.ok(carModelService.getVariantsByMakeAndModel(make, model));
    }

    @GetMapping("/fuel-types")
    public ResponseEntity<List<String>> getAllFuelTypes() {
        return ResponseEntity.ok(carModelService.getAllFuelTypes());
    }

    @GetMapping("/fuel-types/{fuelType}/models")
    public ResponseEntity<List<CarModel>> getModelsByFuelType(@PathVariable String fuelType) {
        return ResponseEntity.ok(carModelService.getModelsByFuelType(fuelType));
    }

    @GetMapping("/body-types")
    public ResponseEntity<List<String>> getAllBodyTypes() {
        return ResponseEntity.ok(carModelService.getAllBodyTypes());
    }

    @GetMapping("/body-types/{bodyType}/models")
    public ResponseEntity<List<CarModel>> getModelsByBodyType(@PathVariable String bodyType) {
        return ResponseEntity.ok(carModelService.getModelsByBodyType(bodyType));
    }

    @GetMapping("/transmission-types")
    public ResponseEntity<List<String>> getAllTransmissionTypes() {
        return ResponseEntity.ok(carModelService.getAllTransmissionTypes());
    }

    @GetMapping("/transmission-types/{transmissionType}/models")
    public ResponseEntity<List<CarModel>> getModelsByTransmissionType(@PathVariable String transmissionType) {
        return ResponseEntity.ok(carModelService.getModelsByTransmissionType(transmissionType));
    }
    
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkModelExists(
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam String variant) {
        boolean exists = carModelService.existsByMakeAndModelAndVariant(make, model, variant);
        return ResponseEntity.ok(exists);
    }
}