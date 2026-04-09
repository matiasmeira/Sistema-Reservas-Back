package com.matiasmeira.back_reservas.booking.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.matiasmeira.back_reservas.booking.model.ModuloFisico;

@Repository
public interface ModuloFisicoRepository extends JpaRepository<ModuloFisico, Long> {
    List<ModuloFisico> findByEstablecimientoIdAndEstado(Long id, String estado);

    @Query("SELECT m FROM ModuloFisico m WHERE m.establecimiento.id = :establecimientoId AND m.estado = 'ACTIVO' AND NOT EXISTS (SELECT ar FROM AsignacionReserva ar WHERE ar.moduloFisico.id = m.id AND ar.fecha = :fecha AND ar.horaInicio < :horaFin AND ar.horaFin > :horaInicio)")
    List<ModuloFisico> findAvailableModulos(@Param("establecimientoId") Long establecimientoId, @Param("fecha") LocalDate fecha, @Param("horaInicio") LocalTime horaInicio, @Param("horaFin") LocalTime horaFin);
}