package com.wheelshift.repository;

import com.wheelshift.model.CarDetailedSpecs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarDetailedSpecsRepository extends JpaRepository<CarDetailedSpecs, Long> {

}