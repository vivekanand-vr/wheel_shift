package com.wheelshift.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_transactions")
@Data
public class FinancialTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    @JsonBackReference("car-transactions")
    private Car car;
    
    @Column(nullable = false)
    private String transactionType;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private LocalDate transactionDate;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String vendorName;
    
    private String receiptUrl;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}