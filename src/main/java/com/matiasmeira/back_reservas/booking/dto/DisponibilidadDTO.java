package com.matiasmeira.back_reservas.booking.dto;

import java.time.LocalTime;

/**
 * DTO que representa un slot de tiempo disponible para una reserva.
 * Se utiliza en la respuesta de consulta de disponibilidad.
 */
public record DisponibilidadDTO(
    LocalTime horaInicio,
    LocalTime horaFin,
    boolean disponible,
    String razonNoDisponible
) {}
