package com.matiasmeira.back_reservas.booking.dto;

import com.matiasmeira.back_reservas.booking.model.EstadoModulo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ModuloFisicoRequestDTO(
    @NotBlank String nombre,
    @NotNull EstadoModulo estado,
    @NotNull @Positive Long establecimientoId
) {}