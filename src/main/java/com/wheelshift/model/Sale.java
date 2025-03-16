package com.wheelshift.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales", indexes = {
    @Index(name = "idx_sale_car", columnList = "car_id"),
    @Index(name = "idx_sale_client", columnList = "client_id"),
    @Index(name = "idx_sale_employee", columnList = "handled_by_id"),
    @Index(name = "idx_sale_date", columnList = "sale_date"),
    @Index(name = "idx_sale_payment_method", columnList = "payment_method")
})
@Data
public class Sale {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "car_id", nullable = false, unique = true)
    @JsonBackReference("car-sale")
    private Car car;
    
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonBackReference("client-sales")
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "handled_by_id", nullable = false)
    @JsonBackReference("employee-sales")
    private Employee handledBy;
    
    @Column(nullable = false)
    private LocalDate saleDate;
    
    @Column(nullable = false)
    private BigDecimal salePrice;
    
    private BigDecimal commissionRate;
    
    private BigDecimal totalCommission;
    
    private String paymentMethod;
    
    private String saleDocumentsUrl;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}