package com.wheelshift.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "car_inspections", indexes = {
    @Index(name = "idx_inspection_car", columnList = "car_id"),
    @Index(name = "idx_inspection_date", columnList = "inspectionDate"),
    @Index(name = "idx_inspection_pass", columnList = "inspectionPass")
})
@Data
public class CarInspection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    @JsonBackReference("car-inspections")
    private Car car;
    
    @Column(nullable = false)
    private LocalDate inspectionDate;
    
    private String inspectorName;
    
    private String overallCondition;
    
    @Column(columnDefinition = "TEXT")
    private String exteriorCondition;
    
    @Column(columnDefinition = "TEXT")
    private String interiorCondition;
    
    @Column(columnDefinition = "TEXT")
    private String mechanicalCondition;
    
    @Column(columnDefinition = "TEXT")
    private String electricalCondition;
    
    @Column(columnDefinition = "TEXT")
    private String accidentHistory;
    
    @Column(columnDefinition = "TEXT")
    private String requiredRepairs;
    
    private BigDecimal estimatedRepairCost;
    
    private Boolean inspectionPass = false;
    
    @Lob
    private byte[] inspectionReportPdf;
    
    private String inspectionReportFilename;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}