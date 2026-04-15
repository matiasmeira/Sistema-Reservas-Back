package com.matiasmeira.back_reservas.booking.dto;

import java.util.List;

public record CombinacionPosibleResponseDTO(
    Long id,
    String nombre,
    Long productoCanchaId,
    String productoCanchaNombre,
    List<Long> modulosFisicosIds,
    List<String> modulosFisicosNombres
) {}