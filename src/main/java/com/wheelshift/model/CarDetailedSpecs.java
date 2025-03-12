package com.wheelshift.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "car_detailed_specs")
@Data
public class CarDetailedSpecs {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "car_id", unique = true)
    @JsonBackReference("car-specs")
    private Car car;
    
    private Integer doors;
    
    private Integer seats;
    
    private BigDecimal cargoCapacityLiters;
    
    private BigDecimal acceleration0To100;
    
    private Integer topSpeed;
    
    @ElementCollection
    @CollectionTable(name = "car_additional_features", 
                    	joinColumns = @JoinColumn(name = "car_specs_id"))
    @MapKeyColumn(name = "feature_name")
    @Column(name = "feature_value")
    private Map<String, String> additionalFeatures = new HashMap<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}