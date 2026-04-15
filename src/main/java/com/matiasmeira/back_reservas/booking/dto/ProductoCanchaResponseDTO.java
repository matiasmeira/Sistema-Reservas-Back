package com.matiasmeira.back_reservas.booking.dto;

import com.matiasmeira.back_reservas.booking.model.Deporte;

public record ProductoCanchaResponseDTO(
    Long id,
    String nombre,
    Deporte deporte,
    String superficie,
    int modulosNecesarios,
    int duracionMinima,
    int intervaloPaso,
    Long establecimientoId,
    String establecimientoNombre
) {}