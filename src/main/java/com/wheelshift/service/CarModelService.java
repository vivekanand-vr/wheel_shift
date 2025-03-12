package com.wheelshift.service;

import com.wheelshift.model.CarModel;
import com.wheelshift.repository.CarModelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CarModelService {
    
    private final CarModelRepository carModelRepository;
    
    public CarModelService(CarModelRepository carModelRepository) {
        this.carModelRepository = carModelRepository;
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

    public List<CarModel> findAllCarModels() {
        return carModelRepository.findAll();
    }
    
    public Optional<CarModel> findById(Integer id) {
        return carModelRepository.findById(id);
    }
    
    @Transactional
    public CarModel saveCarModel(CarModel carModel) {
        return carModelRepository.save(carModel);
    }
    
    @Transactional
    public CarModel updateCarModel(CarModel carModel) {
        if (carModel.getId() == null) {
            throw new IllegalArgumentException("Car model ID cannot be null for update operation");
        }
        return carModelRepository.save(carModel);
    }

    @Transactional
    public void deleteCarModel(Integer id) {
        carModelRepository.deleteById(id);
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
    
    public Optional<List<CarModel>> findByMakeAndModel(String make, String model) {
        return carModelRepository.findByMakeAndModel(make, model);
    }
    
    public Optional<CarModel> findByMakeAndModelAndVariant(String make, String model, String variant) {
        return carModelRepository.findByMakeAndModelAndVariant(make, model, variant);
    }
    
    public List<CarModel> getModelsByMake(String make) {
        return carModelRepository.findByMake(make);
    }
    
    public List<String> getAllMakes() {
        return carModelRepository.findAllDistinctMakes();
    }
    
    public List<String> getModelNamesByMake(String make) {
        return carModelRepository.findAllModelsByMake(make);
    }
    
    public List<String> getVariantsByMakeAndModel(String make, String model) {
        return carModelRepository.findAllVariantsByMakeAndModel(make, model);
    }
    
    public List<CarModel> getModelsByFuelType(String fuelType) {
        return carModelRepository.findByFuelType(fuelType);
    }
    
    public List<CarModel> getModelsByBodyType(String bodyType) {
        return carModelRepository.findByBodyType(bodyType);
    }
    
    public List<CarModel> getModelsByTransmissionType(String transmissionType) {
        return carModelRepository.findByTransmissionType(transmissionType);
    }
    
    public List<String> getAllBodyTypes() {
        return carModelRepository.findAllDistinctBodyTypes();
    }
    
    public List<String> getAllFuelTypes() {
        return carModelRepository.findAllDistinctFuelTypes();
    }
    
    public List<String> getAllTransmissionTypes() {
        return carModelRepository.findAllDistinctTransmissionTypes();
    }
    
    public boolean existsById(Integer id) {
        return carModelRepository.existsById(id);
    }

    public boolean existsByMakeAndModelAndVariant(String make, String model, String variant) {
        return carModelRepository.findByMakeAndModelAndVariant(make, model, variant).isPresent();
    }
}