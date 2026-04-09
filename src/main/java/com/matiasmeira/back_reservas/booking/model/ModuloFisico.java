package com.matiasmeira.back_reservas.booking.model;

import com.matiasmeira.back_reservas.auth.model.EstadoModulo;
import com.matiasmeira.back_reservas.establecimiento.model.Establecimiento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "modulos_fisicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuloFisico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoModulo estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establecimiento_id", nullable = false)
    private Establecimiento establecimiento;

    @OneToMany(mappedBy = "moduloFisico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AsignacionReserva> asignacionesReserva;

    @OneToMany(mappedBy = "moduloFisico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Bloqueo> bloqueos;

    @ManyToMany(mappedBy = "modulosFisicos")
    @JsonIgnore
    private List<CombinacionPosible> combinacionesPosibles;
}