package com.matiasmeira.back_reservas.booking.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CombinacionPosibleRequestDTO(
    @NotBlank String nombre,
    @NotNull @Positive Long productoCanchaId,
    @NotEmpty List<@NotNull @Positive Long> modulosFisicosIds
) {}