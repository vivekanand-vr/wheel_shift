package com.wheelshift.repository;

import com.wheelshift.model.Car;
import com.wheelshift.model.Client;
import com.wheelshift.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByCar(Car car);
    
    List<Reservation> findByStatus(String status);
    
    List<Reservation> findByDepositPaid(Boolean depositPaid);
    
    List<Reservation> findByClient(Client client);
    
    List<Reservation> findByClientEmailContainingIgnoreCase(String email);
    
    List<Reservation> findByClientNameContainingIgnoreCase(String name);
    
    List<Reservation> findByReservationDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Reservation> findByExpiryDateBefore(LocalDateTime date);
    
    List<Reservation> findByStatusAndExpiryDateBefore(String status, LocalDateTime cutoffDate);
    
    List<Reservation> findByCarIdAndStatus(Long carId, String status);
    
    @Query("SELECT r FROM Reservation r WHERE r.car.id = :carId AND r.status = 'ACTIVE'")
    List<Reservation> findActiveReservationsByCar(Long carId);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.status = :status")
    Long countByStatus(String status);
    
    @Query("SELECT r FROM Reservation r WHERE r.expiryDate < CURRENT_TIMESTAMP AND r.status = 'ACTIVE'")
    List<Reservation> findExpiredReservations();
    
    @Query("SELECT MONTH(r.reservationDate), COUNT(r) FROM Reservation r " +
           "WHERE YEAR(r.reservationDate) = :year GROUP BY MONTH(r.reservationDate)")
    List<Object[]> countReservationsByMonth(int year);
}