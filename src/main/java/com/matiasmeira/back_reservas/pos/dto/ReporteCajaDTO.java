package com.matiasmeira.back_reservas.pos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteCajaDTO(
    LocalDate fecha,
    BigDecimal totalVentas,
    int numeroVentas
) {}