package com.matiasmeira.back_reservas.booking.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaRequestDTO(
    LocalDate fechaReserva,
    LocalTime horaInicio,
    LocalTime horaFin,
    Long productoCanchaId,
    Long usuarioId,
    boolean permitirReasignacion
) {}