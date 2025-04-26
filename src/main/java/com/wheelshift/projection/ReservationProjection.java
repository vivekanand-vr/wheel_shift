package com.wheelshift.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ReservationProjection {
    Long getId();
    LocalDateTime getReservationDate();
    LocalDateTime getExpiryDate();
    String getStatus();
    BigDecimal getDepositAmount();
    Boolean getDepositPaid();
    String getNotes();
    
    CarProjection getCar();
    ClientProjection getClient();
    
    interface CarProjection {
        Long getId();
        String getVinNumber();
        String getRegistrationNumber();
        Integer getYear();
        
        // Add car model info
        CarModelProjection getCarModel();
        
        interface CarModelProjection {
            Long getId();
            String getModel();
            String getVariant();
            String getMake();
        }
    }
    
    interface ClientProjection {
        Long getId();
        String getName();
        String getEmail();
        String getPhone();
    }
}