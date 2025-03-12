package com.wheelshift.repository;

import com.wheelshift.model.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarModelRepository extends JpaRepository<CarModel, Integer> {
    
    Optional<List<CarModel>> findByMakeAndModel(String make, String model);

    Optional<CarModel> findByMakeAndModelAndVariant(String make, String model, String variant);

    List<CarModel> findByMake(String make);

    @Query("SELECT DISTINCT c.make FROM CarModel c ORDER BY c.make")
    List<String> findAllDistinctMakes();

    @Query("SELECT DISTINCT c.model FROM CarModel c WHERE c.make = :make ORDER BY c.model")
    List<String> findAllModelsByMake(@Param("make") String make);

    @Query("SELECT c.variant FROM CarModel c WHERE c.make = :make AND c.model = :model AND c.variant IS NOT NULL")
    List<String> findAllVariantsByMakeAndModel(@Param("make") String make, @Param("model") String model);

    List<CarModel> findByFuelType(String fuelType);
 
    List<CarModel> findByBodyType(String bodyType);

    List<CarModel> findByTransmissionType(String transmissionType);

    @Query("SELECT DISTINCT c.bodyType FROM CarModel c WHERE c.bodyType IS NOT NULL")
    List<String> findAllDistinctBodyTypes();

    @Query("SELECT DISTINCT c.fuelType FROM CarModel c WHERE c.fuelType IS NOT NULL")
    List<String> findAllDistinctFuelTypes();

    @Query("SELECT DISTINCT c.transmissionType FROM CarModel c WHERE c.transmissionType IS NOT NULL")
    List<String> findAllDistinctTransmissionTypes();
}