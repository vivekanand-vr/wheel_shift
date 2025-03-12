package com.wheelshift.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "storage_locations")
@Data
public class StorageLocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String address;
    
    private String contactPerson;
    
    private String contactNumber;
    
    private Integer totalCapacity;
    
    private Integer currentVehicleCount = 0;
    
    @OneToMany(mappedBy = "storageLocation")
    @JsonBackReference
    private List<Car> cars = new ArrayList<>();
}