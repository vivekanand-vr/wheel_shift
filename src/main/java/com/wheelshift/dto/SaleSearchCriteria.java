package com.wheelshift.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SaleSearchCriteria {
    private String searchTerm;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long clientId;
    private Long employeeId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String paymentMethod;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}