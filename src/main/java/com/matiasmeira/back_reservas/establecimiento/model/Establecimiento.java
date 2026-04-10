package com.matiasmeira.back_reservas.establecimiento.model;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matiasmeira.back_reservas.auth.model.Usuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @PrePersist
    protected void onCreate() {
        if (this.estado == null) {
            this.estado = EstadoEstablecimiento.ACTIVO;
        }
    }
}