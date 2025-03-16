package com.wheelshift.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients", indexes = {
    @Index(name = "idx_client_email", columnList = "email", unique = true),
    @Index(name = "idx_client_phone", columnList = "phone"),
    @Index(name = "idx_client_status", columnList = "status")
})
@Data
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String phone;
    
    private String location;
    
    @Column(nullable = false)
    private String status;
    
    private Integer totalPurchases = 0;
    
    private LocalDate lastPurchase;
    
    @OneToMany(mappedBy = "client")
    @JsonManagedReference("client-inquiries")
    private List<Inquiry> inquiries = new ArrayList<>();
    
    @OneToMany(mappedBy = "client")
    @JsonManagedReference("client-reservations")
    private List<Reservation> reservations = new ArrayList<>();
    
    @OneToMany(mappedBy = "client")
    @JsonManagedReference("client-sales")
    private List<Sale> sales = new ArrayList<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
