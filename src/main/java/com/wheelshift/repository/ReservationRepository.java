package com.wheelshift.repository;

import com.wheelshift.model.Car;
import com.wheelshift.model.Client;
import com.wheelshift.model.Reservation;
import com.wheelshift.projection.ReservationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // Existing methods remain the same
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
    
    // Updated methods using projections
    @Query("SELECT r FROM Reservation r JOIN FETCH r.car c JOIN FETCH r.client cl JOIN FETCH c.carModel WHERE r.id = :id")
    Optional<Reservation> findByIdWithCarAndClient(@Param("id") Long id);
    
    @Query("SELECT r FROM Reservation r JOIN FETCH r.car c JOIN FETCH r.client cl JOIN FETCH c.carModel")
    List<Reservation> findAllWithCarAndClient();
    
    // New methods using projections
    Optional<ReservationProjection> findProjectionById(Long id);
    
    List<ReservationProjection> findProjectionsByCar(Car car);
    
    List<ReservationProjection> findProjectionsByStatus(String status);
    
    @Query("SELECT r FROM Reservation r JOIN r.car c JOIN r.client cl JOIN c.carModel WHERE r.id = :id")
    Optional<ReservationProjection> findProjectionByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT r FROM Reservation r JOIN r.car c JOIN r.client cl JOIN c.carModel")
    List<ReservationProjection> findAllProjectionsWithDetails();
    
    @Query("SELECT r FROM Reservation r JOIN r.car c JOIN r.client cl JOIN c.carModel WHERE c.id = :carId")
    List<ReservationProjection> findProjectionsByCarId(@Param("carId") Long carId);
    
    @Query("SELECT r FROM Reservation r JOIN r.car c JOIN r.client cl JOIN c.carModel WHERE cl.id = :clientId")
    List<ReservationProjection> findProjectionsByClientId(@Param("clientId") Long clientId);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.status = :status")
    Long countByStatus(String status);
    
    @Query("SELECT r FROM Reservation r WHERE r.expiryDate < CURRENT_TIMESTAMP AND r.status = 'ACTIVE'")
    List<Reservation> findExpiredReservations();
    
    @Query("SELECT MONTH(r.reservationDate), COUNT(r) FROM Reservation r " +
            "WHERE YEAR(r.reservationDate) = :year GROUP BY MONTH(r.reservationDate)")
     List<Object[]> countReservationsByMonth(int year);
}