package com.wheelshift.dto;

import com.wheelshift.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEventDTO {
    private Long id;
    private String title;
    private String start; // ISO-8601 string format for frontend compatibility
    private String end; // ISO-8601 string format for frontend compatibility 
    private String backgroundColor;
    private String borderColor;
    private EventType eventType;
    private String customerName;
    private String carDetails;
    
    // Extended properties for frontend
    public ExtendedProps getExtendedProps() {
        return new ExtendedProps(
            eventType.getValue(),
            customerName,
            carDetails
        );
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtendedProps {
        private String type;
        private String customerName;
        private String carDetails;
    }
}