package com.wheelshift.repository.spec;

import com.wheelshift.model.Client;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ClientSpecification {

    public static Specification<Client> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }
    
    public static Specification<Client> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }
    
    public static Specification<Client> hasPhone(String phone) {
        return (root, query, cb) -> {
            if (phone == null || phone.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("phone")), "%" + phone.toLowerCase() + "%");
        };
    }
    
    public static Specification<Client> hasLocation(String location) {
        return (root, query, cb) -> {
            if (location == null || location.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
        };
    }
    
    public static Specification<Client> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.trim().isEmpty()) {
                return null;
            }
            return cb.equal(cb.lower(root.get("status")), status.toLowerCase());
        };
    }
    
    public static Specification<Client> hasTotalPurchasesBetween(Integer minTotalPurchases, Integer maxTotalPurchases) {
        return (root, query, cb) -> {
            if (minTotalPurchases == null && maxTotalPurchases == null) {
                return null;
            }
            
            if (minTotalPurchases == null) {
                return cb.lessThanOrEqualTo(root.get("totalPurchases"), maxTotalPurchases);
            }
            
            if (maxTotalPurchases == null) {
                return cb.greaterThanOrEqualTo(root.get("totalPurchases"), minTotalPurchases);
            }
            
            return cb.between(root.get("totalPurchases"), minTotalPurchases, maxTotalPurchases);
        };
    }
    
    public static Specification<Client> hasLastPurchaseBetween(LocalDate fromDate, LocalDate toDate) {
        return (root, query, cb) -> {
            if (fromDate == null && toDate == null) {
                return null;
            }
            
            if (fromDate == null) {
                return cb.lessThanOrEqualTo(root.get("lastPurchase"), toDate);
            }
            
            if (toDate == null) {
                return cb.greaterThanOrEqualTo(root.get("lastPurchase"), fromDate);
            }
            
            return cb.between(root.get("lastPurchase"), fromDate, toDate);
        };
    }
    
    public static Specification<Client> containsText(String searchText) {
        return (root, query, cb) -> {
            if (searchText == null || searchText.trim().isEmpty()) {
                return null;
            }
            
            String likePattern = "%" + searchText.toLowerCase() + "%";
            
            return cb.or(
                cb.like(cb.lower(root.get("name")), likePattern),
                cb.like(cb.lower(root.get("email")), likePattern),
                cb.like(cb.lower(root.get("phone")), likePattern),
                cb.like(cb.lower(root.get("location")), likePattern)
            );
        };
    }
}