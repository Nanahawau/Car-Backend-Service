package com.udacity.vehicles.domain.car;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

   Optional<Car> findById(Long id);
   Car findCarById(Long id);
}
