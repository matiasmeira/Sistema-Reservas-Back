package com.matiasmeira.back_reservas.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.matiasmeira.back_reservas.booking.model.EstadoReserva;

public record ReservaResponseDTO(
    Long id,
    LocalDate fechaReserva,
    LocalTime horaInicio,
    LocalTime horaFin,
    BigDecimal precioTotal,
    EstadoReserva estado,
    Long usuarioId,
    Long productoCanchaId,
    List<Long> modulosAsignadosIds,
    Long pagoId
) {}