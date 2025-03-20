package com.wheelshift.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ClientSearchCriteria {
    private String name;
    private String email;
    private String phone;
    private String location;
    private String status;
    private Integer minTotalPurchases;
    private Integer maxTotalPurchases;
    private LocalDate lastPurchaseFrom;
    private LocalDate lastPurchaseTo;
    private String searchText;
}