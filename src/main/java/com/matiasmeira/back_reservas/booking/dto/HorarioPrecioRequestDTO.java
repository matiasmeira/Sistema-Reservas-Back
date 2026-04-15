package com.matiasmeira.back_reservas.booking.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record HorarioPrecioRequestDTO(
    @NotNull @Min(1) @Max(7) int diaSemana,
    @NotNull LocalTime horaInicio,
    @NotNull LocalTime horaFin,
    @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal precioHora,
    @NotNull @Positive Long productoCanchaId
) {}