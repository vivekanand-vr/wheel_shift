package com.wheelshift.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "car_models", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"make", "model", "variant"}, 
    		name = "uk_car_model_make_model_variant")
})
@Data
public class CarModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String make;
    
    @Column(nullable = false)
    private String model;
    private String variant;
    private String emissionNorm;
    private String fuelTankCapacity;
    private String fuelType;
    private String bodyType;
    private Integer gears;
    private String transmissionType;
}