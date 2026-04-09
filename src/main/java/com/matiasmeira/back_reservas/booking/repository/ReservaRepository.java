package com.matiasmeira.back_reservas.booking.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.matiasmeira.back_reservas.booking.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByFechaReservaAndEstadoNot(LocalDate fecha, String estado);

    @Query("SELECT r FROM Reserva r WHERE r.productoCancha.id = :productoCanchaId AND r.estado != 'CANCELADAS' AND ((r.horaInicio < :horaFin AND r.horaFin > :horaInicio))")
    List<Reserva> findOverlappingReservas(@Param("productoCanchaId") Long productoCanchaId, @Param("horaInicio") LocalTime horaInicio, @Param("horaFin") LocalTime horaFin);
}