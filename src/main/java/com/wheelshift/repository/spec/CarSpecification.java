package com.wheelshift.repository.spec;

import com.wheelshift.model.Car;
import com.wheelshift.model.CarModel;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CarSpecification {

    // Helper method to get or create a join
    @SuppressWarnings("unchecked")
	private static Join<Car, CarModel> getCarModelJoin(Root<Car> root) {
        return root.getJoins().stream()
            .filter(j -> j.getAttribute().getName().equals("carModel"))
            .map(j -> (Join<Car, CarModel>) j)
            .findFirst()
            .orElseGet(() -> root.join("carModel", JoinType.LEFT));
    }

    public static Specification<Car> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.trim().isEmpty()) {
                return null;
            }
            return cb.equal(cb.lower(root.get("currentStatus")), status.toLowerCase());
        };
    }
    
    public static Specification<Car> hasMake(String make) {
        return (root, query, cb) -> {
            if (make == null || make.trim().isEmpty()) {
                return null;
            }
            Join<Car, CarModel> modelJoin = getCarModelJoin(root);
            return cb.equal(cb.lower(modelJoin.get("make")), make.toLowerCase());
        };
    }
    
    public static Specification<Car> hasModel(String model) {
        return (root, query, cb) -> {
            if (model == null || model.trim().isEmpty()) {
                return null;
            }
            Join<Car, CarModel> modelJoin = getCarModelJoin(root);
            return cb.equal(cb.lower(modelJoin.get("model")), model.toLowerCase());
        };
    }
    
    public static Specification<Car> hasBodyType(String bodyType) {
        return (root, query, cb) -> {
            if (bodyType == null || bodyType.trim().isEmpty()) {
                return null;
            }
            Join<Car, CarModel> modelJoin = getCarModelJoin(root);
            return cb.equal(cb.lower(modelJoin.get("bodyType")), bodyType.toLowerCase());
        };
    }
    
    public static Specification<Car> hasFuelType(String fuelType) {
        return (root, query, cb) -> {
            if (fuelType == null || fuelType.trim().isEmpty()) {
                return null;
            }
            Join<Car, CarModel> modelJoin = getCarModelJoin(root);
            return cb.equal(cb.lower(modelJoin.get("fuelType")), fuelType.toLowerCase());
        };
    }
    
    public static Specification<Car> hasTransmissionType(String transmissionType) {
        return (root, query, cb) -> {
            if (transmissionType == null || transmissionType.trim().isEmpty()) {
                return null;
            }
            Join<Car, CarModel> modelJoin = getCarModelJoin(root);
            return cb.equal(cb.lower(modelJoin.get("transmissionType")), transmissionType.toLowerCase());
        };
    }
    
    public static Specification<Car> hasColor(String color) {
        return (root, query, cb) -> {
            if (color == null || color.trim().isEmpty()) {
                return null;
            }
            return cb.equal(cb.lower(root.get("color")), color.toLowerCase());
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
            if (fromYear == null && toYear == null) {
                return null;
            }
            
            if (fromYear == null) {
                return cb.lessThanOrEqualTo(root.get("year"), toYear);
            }
            
            if (toYear == null) {
                return cb.greaterThanOrEqualTo(root.get("year"), fromYear);
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
            // Use a left join to ensure cars without locations are still included in other queries
            return cb.equal(root.get("storageLocation").get("id"), locationId);
        };
    }
    
    public static Specification<Car> containsText(String searchText) {
        return (root, query, cb) -> {
            if (searchText == null || searchText.trim().isEmpty()) {
                return null;
            }
            
            String likePattern = "%" + searchText.toLowerCase() + "%";
            
            Join<Car, CarModel> modelJoin = getCarModelJoin(root);
            
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