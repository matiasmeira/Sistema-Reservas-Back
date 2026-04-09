package com.matiasmeira.back_reservas.pos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.matiasmeira.back_reservas.pagos.model.MetodoPago;

/**
 * DTO que representa la respuesta de una venta registrada en el POS.
 */
public record VentaResponseDTO(
    Long id,
    Long establecimientoId,
    Long usuarioId,
    BigDecimal montoTotal,
    MetodoPago metodoPago,
    LocalDateTime fechaHora,
    List<DetalleVentaRequestDTO> detalles,
    String numeroTransaccion
) {}
