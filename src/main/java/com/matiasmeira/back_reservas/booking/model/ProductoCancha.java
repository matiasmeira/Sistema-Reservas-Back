package com.matiasmeira.back_reservas.booking.model;

import com.matiasmeira.back_reservas.auth.model.Deporte;
import com.matiasmeira.back_reservas.establecimiento.model.Establecimiento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "productos_cancha")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoCancha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Deporte deporte;

    @Column(nullable = false)
    private String superficie;

    @Column(nullable = false)
    private int modulosNecesarios;

    @Column(nullable = false)
    private int duracionMinima;

    @Column(nullable = false)
    private int intervaloPaso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establecimiento_id", nullable = false)
    private Establecimiento establecimiento;

    @OneToMany(mappedBy = "productoCancha", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<HorarioPrecio> horariosPrecio;

    @OneToMany(mappedBy = "productoCancha", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "productoCancha", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CombinacionPosible> combinacionesPosibles;
}