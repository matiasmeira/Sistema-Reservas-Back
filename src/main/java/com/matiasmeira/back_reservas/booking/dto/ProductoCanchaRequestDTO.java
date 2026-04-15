package com.matiasmeira.back_reservas.booking.dto;

import com.matiasmeira.back_reservas.booking.model.Deporte;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductoCanchaRequestDTO(
    @NotBlank String nombre,
    @NotNull Deporte deporte,
    @NotBlank String superficie,
    @NotNull @Positive int modulosNecesarios,
    @NotNull @Positive int duracionMinima,
    @NotNull @Positive int intervaloPaso,
    @NotNull @Positive Long establecimientoId
) {}