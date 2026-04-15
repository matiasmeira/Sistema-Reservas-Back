package com.matiasmeira.back_reservas.booking.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

public record HorarioPrecioResponseDTO(
    Long id,
    int diaSemana,
    LocalTime horaInicio,
    LocalTime horaFin,
    BigDecimal precioHora,
    Long productoCanchaId,
    String productoCanchaNombre
) {}