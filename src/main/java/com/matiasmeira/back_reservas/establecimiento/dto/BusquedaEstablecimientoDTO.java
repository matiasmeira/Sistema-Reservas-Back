package com.matiasmeira.back_reservas.establecimiento.dto;

import com.matiasmeira.back_reservas.booking.model.Deporte;

public record BusquedaEstablecimientoDTO(
    Double latitud,
    Double longitud,
    Double radioKm,
    Deporte deporte
) {}