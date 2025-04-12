package com.wheelshift.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

/**
 * DTO for transferring Sale data to the client
 * Includes only necessary fields without exposing full entity relationships
 */
@Data
public class SaleDTO {
    private Long id;
    
    // Car details (subset)
    private Long carId;
    private String carMake;
    private String carModel;
    private Integer carYear;
    
    // Client details (subset)
    private Long clientId;
    private String clientName;
    private String clientEmail;
    
    // Employee details (subset)
    private Long employeeId;
    private String employeeFullName;
    
    private LocalDate saleDate;
    private BigDecimal salePrice;
    private BigDecimal commissionRate;
    private BigDecimal totalCommission;
    private String paymentMethod;
    private String saleDocumentsUrl;
}