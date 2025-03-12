package com.wheelshift.dto;

import lombok.Data;

import java.util.Map;

@Data
public class LocationStatistics {
    private Long totalLocations;
    private Long emptyLocations;
    private Long fullLocations;
    private Integer totalCapacity;
    private Integer totalVehiclesStored;
    private Double averageOccupancyPercentage;
    private Map<String, Double> occupancyByLocation;
    private Map<String, Map<String, Long>> carMakesByLocation;
    private Map<String, Map<String, Long>> carBodyTypesByLocation;
}