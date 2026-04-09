package com.matiasmeira.back_reservas.pagos.model;

import com.matiasmeira.back_reservas.auth.model.EstadoPago;
import com.matiasmeira.back_reservas.booking.model.Reserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

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