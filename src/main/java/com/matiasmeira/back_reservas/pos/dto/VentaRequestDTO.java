package com.matiasmeira.back_reservas.pos.dto;

import java.util.List;

import com.matiasmeira.back_reservas.pagos.model.MetodoPago;

public record VentaRequestDTO(
    Long establecimientoId,
    Long usuarioId,
    MetodoPago metodoPago,
    List<DetalleVentaRequestDTO> detalles
) {}