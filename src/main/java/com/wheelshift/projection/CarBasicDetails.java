package com.wheelshift.projection;

import java.math.BigDecimal;

public interface CarBasicDetails {
    Long getId();
    String getVinNumber();
    String getRegistrationNumber();
    String getColor();
    Integer getYear();
    String getCurrentStatus();
    BigDecimal getMileage();
    BigDecimal getEngineCapacity();
    CarModelInfo getCarModel();

    interface CarModelInfo {
        String getMake();
        String getModel();
        String getVariant();
        String getFuelType();
        String getBodyType();
    }
}