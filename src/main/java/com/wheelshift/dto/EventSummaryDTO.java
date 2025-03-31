package com.wheelshift.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSummaryDTO {
    private int totalAppointments;
    private Map<String, Integer> countByType;
}