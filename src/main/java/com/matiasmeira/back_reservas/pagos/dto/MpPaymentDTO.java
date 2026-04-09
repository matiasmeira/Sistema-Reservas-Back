package com.matiasmeira.back_reservas.pagos.dto;

import java.math.BigDecimal;

public record MpPaymentDTO(
    String paymentId,
    String status,
    BigDecimal amount
) {}