package com.wheelshift.repository.spec;

import com.wheelshift.model.Car;
import com.wheelshift.model.CarModel;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CarSpecification {

    public static Specification<Car> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            return cb.equal(root.get("currentStatus"), status);
        };
    }
    
    public static Specification<Car> hasMake(String make) {
        return (root, query, cb) -> {
            if (make == null) {
                return null;
            }
            Join<Car, CarModel> modelJoin = root.join("carModel");
            return cb.equal(modelJoin.get("make"), make);
        };
    }
    
    public static Specification<Car> hasModel(String model) {
        return (root, query, cb) -> {
            if (model == null) {
                return null;
            }
            Join<Car, CarModel> modelJoin = root.join("carModel");
            return cb.equal(modelJoin.get("model"), model);
        };
    }
    
    public static Specification<Car> hasBodyType(String bodyType) {
        return (root, query, cb) -> {
            if (bodyType == null) {
                return null;
            }
            Join<Car, CarModel> modelJoin = root.join("carModel");
            return cb.equal(modelJoin.get("bodyType"), bodyType);
        };
    }
    
    public static Specification<Car> hasFuelType(String fuelType) {
        return (root, query, cb) -> {
            if (fuelType == null) {
                return null;
            }
            Join<Car, CarModel> modelJoin = root.join("carModel");
            return cb.equal(modelJoin.get("fuelType"), fuelType);
        };
    }
    
    public static Specification<Car> hasTransmissionType(String transmissionType) {
        return (root, query, cb) -> {
            if (transmissionType == null) {
                return null;
            }
            Join<Car, CarModel> modelJoin = root.join("carModel");
            return cb.equal(modelJoin.get("transmissionType"), transmissionType);
        };
    }
    
    public static Specification<Car> hasColor(String color) {
        return (root, query, cb) -> {
            if (color == null) {
                return null;
            }
            return cb.equal(root.get("color"), color);
        };
    }
    
    public static Specification<Car> hasYear(Integer year) {
        return (root, query, cb) -> {
            if (year == null) {
                return null;
            }
            return cb.equal(root.get("year"), year);
        };
    }
    
    public static Specification<Car> hasYearBetween(Integer fromYear, Integer toYear) {
        return (root, query, cb) -> {
            if (fromYear == null || toYear == null) {
                return null;
            }
            return cb.between(root.get("year"), fromYear, toYear);
        };
    }
    
    public static Specification<Car> hasMileageBetween(BigDecimal minMileage, BigDecimal maxMileage) {
        return (root, query, cb) -> {
            if (minMileage == null && maxMileage == null) {
                return null;
            }
            
            if (minMileage == null) {
                return cb.lessThanOrEqualTo(root.get("mileage"), maxMileage);
            }
            
            if (maxMileage == null) {
                return cb.greaterThanOrEqualTo(root.get("mileage"), minMileage);
            }
            
            return cb.between(root.get("mileage"), minMileage, maxMileage);
        };
    }
    
    public static Specification<Car> hasPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) {
                return null;
            }
            
            if (minPrice == null) {
                return cb.lessThanOrEqualTo(root.get("sellingPrice"), maxPrice);
            }
            
            if (maxPrice == null) {
                return cb.greaterThanOrEqualTo(root.get("sellingPrice"), minPrice);
            }
            
            return cb.between(root.get("sellingPrice"), minPrice, maxPrice);
        };
    }
    
    public static Specification<Car> purchasedBetween(LocalDate fromDate, LocalDate toDate) {
        return (root, query, cb) -> {
            if (fromDate == null && toDate == null) {
                return null;
            }
            
            if (fromDate == null) {
                return cb.lessThanOrEqualTo(root.get("purchaseDate"), toDate);
            }
            
            if (toDate == null) {
                return cb.greaterThanOrEqualTo(root.get("purchaseDate"), fromDate);
            }
            
            return cb.between(root.get("purchaseDate"), fromDate, toDate);
        };
    }
    
    public static Specification<Car> hasLocationId(Long locationId) {
        return (root, query, cb) -> {
            if (locationId == null) {
                return null;
            }
            return cb.equal(root.get("storageLocation").get("id"), locationId);
        };
    }
    
    public static Specification<Car> containsText(String searchText) {
        return (root, query, cb) -> {
            if (searchText == null || searchText.trim().isEmpty()) {
                return null;
            }
            
            String likePattern = "%" + searchText.toLowerCase() + "%";
            
            Join<Car, CarModel> modelJoin = root.join("carModel");
            
            return cb.or(
                cb.like(cb.lower(root.get("vinNumber")), likePattern),
                cb.like(cb.lower(root.get("registrationNumber")), likePattern),
                cb.like(cb.lower(modelJoin.get("make")), likePattern),
                cb.like(cb.lower(modelJoin.get("model")), likePattern),
                cb.like(cb.lower(modelJoin.get("variant")), likePattern),
                cb.like(cb.lower(root.get("color")), likePattern)
            );
        };
    }
}