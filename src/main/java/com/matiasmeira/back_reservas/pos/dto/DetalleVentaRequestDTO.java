package com.matiasmeira.back_reservas.pos.dto;

public record DetalleVentaRequestDTO(
    Long productoPosId,
    int cantidad
) {}