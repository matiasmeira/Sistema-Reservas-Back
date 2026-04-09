package com.matiasmeira.back_reservas.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "horarios_precio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioPrecio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int diaSemana;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFin;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal precioHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_cancha_id", nullable = false)
    private ProductoCancha productoCancha;
}