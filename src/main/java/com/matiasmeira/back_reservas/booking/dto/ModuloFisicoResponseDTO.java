package com.matiasmeira.back_reservas.booking.dto;

import com.matiasmeira.back_reservas.booking.model.EstadoModulo;

public record ModuloFisicoResponseDTO(
    Long id,
    String nombre,
    EstadoModulo estado,
    Long establecimientoId,
    String establecimientoNombre
) {}