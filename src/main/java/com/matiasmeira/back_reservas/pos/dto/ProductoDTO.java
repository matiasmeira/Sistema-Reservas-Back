package com.matiasmeira.back_reservas.pos.dto;

/**
 * DTO que representa un producto del catálogo de POS.
 * Se utiliza para mostrar el catálogo de productos disponibles.
 */
public record ProductoDTO(
    Long id,
    String nombre,
    java.math.BigDecimal precio,
    boolean activo
) {}
