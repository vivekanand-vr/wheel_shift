package com.wheelshift.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class FinancialStatistics {
    private Map<String, BigDecimal> totalByTransactionType;
    private BigDecimal totalPurchases;
    private BigDecimal totalSales;
    private BigDecimal totalMaintenance;
    private BigDecimal totalRevenue;
    private Map<String, BigDecimal> purchasesByMonth;
    private Map<String, BigDecimal> salesByMonth;
    private Map<String, BigDecimal> spendByVendor;
    private Map<String, BigDecimal> maintenanceByMake;
}