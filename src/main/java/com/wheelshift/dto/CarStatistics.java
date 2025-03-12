package com.wheelshift.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarStatistics {
    private Long totalCars;
    private Long availableCars;
    private Long reservedCars;
    private Long soldCars;
    private BigDecimal averageProfitMargin;
    private BigDecimal averageMileage;
    private Double averageDaysToSell;
    private Map<String, Long> inventoryByMake;
    private Map<String, Long> inventoryByBodyType;
    private Map<Integer, Long> purchaseCountByYear;
    private Map<Integer, Long> purchaseCountByMonth;
    private Map<String, BigDecimal> averageMileageByModel;
    private Map<String, Double> averageDaysToSellByMake;
}