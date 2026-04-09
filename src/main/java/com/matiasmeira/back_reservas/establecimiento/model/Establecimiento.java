package com.matiasmeira.back_reservas.establecimiento.model;

import com.matiasmeira.back_reservas.auth.model.EstadoEstablecimiento;
import com.matiasmeira.back_reservas.auth.model.TipoPlan;
import com.matiasmeira.back_reservas.auth.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "establecimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Establecimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    private Double latitud;

    private Double longitud;

    private LocalTime horaApertura;

    private LocalTime horaCierre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPlan tipoPlan;

    @Column(nullable = false)
    private boolean permitirReasignacion;

    @Column(nullable = false)
    private boolean requiereSena;

    private int porcentajeSena;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEstablecimiento estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
        name = "establecimiento_amenities",
        joinColumns = @JoinColumn(name = "establecimiento_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities;

    @OneToMany(mappedBy = "establecimiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<com.matiasmeira.back_reservas.booking.model.ModuloFisico> modulosFisicos;

    @OneToMany(mappedBy = "establecimiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<com.matiasmeira.back_reservas.booking.model.ProductoCancha> productosCancha;
}