package com.wheelshift.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CarSearchCriteria {
    private String make;
    private String model;
    private String bodyType;
    private String fuelType;
    private String transmissionType;
    private String color;
    private Integer yearFrom;
    private Integer yearTo;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal minMileage;
    private BigDecimal maxMileage;
    private String status;
    private Long locationId;
    private LocalDate purchaseDateFrom;
    private LocalDate purchaseDateTo;
}