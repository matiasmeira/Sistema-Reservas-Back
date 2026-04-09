package com.matiasmeira.back_reservas.establecimiento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matiasmeira.back_reservas.establecimiento.model.Amenity;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}