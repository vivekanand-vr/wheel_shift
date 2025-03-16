package com.wheelshift.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars", indexes = {
    @Index(name = "idx_car_vin", columnList = "vinNumber", unique = true),
    @Index(name = "idx_car_registration", columnList = "registrationNumber"),
    @Index(name = "idx_car_status", columnList = "currentStatus"),
    @Index(name = "idx_car_model", columnList = "model_id"),
    @Index(name = "idx_car_location", columnList = "location_id"),
    @Index(name = "idx_car_year", columnList = "year")
})
@Data
public class Car {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private CarModel carModel;
    
    @Column(nullable = false, unique = true, length = 17)
    private String vinNumber;
    
    private String registrationNumber;
    
    @Column(nullable = false)
    private Integer year;
    
    private String color;
    
    private BigDecimal mileage;
    
    private BigDecimal engineCapacity;
    
    @Column(nullable = false)
    private String currentStatus;
    
    private LocalDate purchaseDate;
    
    private BigDecimal purchasePrice;
    
    private BigDecimal sellingPrice;
    
    @ManyToOne
    @JoinColumn(name = "location_id")
    private StorageLocation storageLocation;
    
    @OneToOne(mappedBy = "car", cascade = CascadeType.PERSIST)
    @JsonManagedReference("car-specs")
    private CarDetailedSpecs detailedSpecs;
    
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    @JsonManagedReference("car-inspections")
    private List<CarInspection> inspections = new ArrayList<>();
    
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    @JsonManagedReference("car-transactions")
    private List<FinancialTransaction> financialTransactions = new ArrayList<>();
    
    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL)
    @JsonManagedReference("car-sale")
    private Sale sale;
    
    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL)
    @JsonManagedReference("car-reservation")
    private Reservation reservation;
    
    @OneToMany(mappedBy = "car")
    @JsonManagedReference("car-inquiries")
    private List<Inquiry> inquiries = new ArrayList<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
