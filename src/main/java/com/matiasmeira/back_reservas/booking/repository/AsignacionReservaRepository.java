package com.matiasmeira.back_reservas.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matiasmeira.back_reservas.booking.model.AsignacionReserva;

@Repository
public interface AsignacionReservaRepository extends JpaRepository<AsignacionReserva, Long> {
}