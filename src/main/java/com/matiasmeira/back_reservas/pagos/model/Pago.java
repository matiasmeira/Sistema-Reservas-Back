package com.matiasmeira.back_reservas.pagos.model;

import java.math.BigDecimal;

import com.matiasmeira.back_reservas.booking.model.Reserva;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal montoTotal;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal comisionPlataforma;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal montoNetoDuenio;

    private String mpPreferenceId;

    private String mpPaymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estadoPago;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;
}