package com.matiasmeira.back_reservas.establecimiento.dto;

import java.time.LocalTime;
import java.util.List;

import com.matiasmeira.back_reservas.establecimiento.model.EstadoEstablecimiento;
import com.matiasmeira.back_reservas.establecimiento.model.TipoPlan;

public record EstablecimientoRequestDTO(
    String nombre,
    String direccion,
    Double latitud,
    Double longitud,
    LocalTime horaApertura,
    LocalTime horaCierre,
    TipoPlan tipoPlan,
    boolean permitirReasignacion,
    boolean requiereSena,
    int porcentajeSena,
    EstadoEstablecimiento estado,
    Long usuarioId,
    List<Long> amenityIds
) {}