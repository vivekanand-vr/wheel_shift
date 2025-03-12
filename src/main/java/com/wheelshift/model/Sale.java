package com.wheelshift.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Data
public class Sale {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "car_id", nullable = false, unique = true)
    @JsonBackReference("car-sale")
    private Car car;
    
    @Column(nullable = false)
    private LocalDate saleDate;
    
    private String buyerName;
    
    private String buyerContact;
    
    @Column(nullable = false)
    private BigDecimal salePrice;
    
    private BigDecimal commissionRate;
    
    private BigDecimal totalCommission;
    
    private String paymentMethod;
    
    private String saleDocumentsUrl;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}