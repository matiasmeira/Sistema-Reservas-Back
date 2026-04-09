package com.matiasmeira.back_reservas.booking.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matiasmeira.back_reservas.booking.model.Bloqueo;

@Repository
public interface BloqueoRepository extends JpaRepository<Bloqueo, Long> {
    List<Bloqueo> findByModuloFisicoIdAndFechaInicioBeforeAndFechaFinAfter(Long moduloFisicoId, LocalDate fechaInicio, LocalDate fechaFin);
}