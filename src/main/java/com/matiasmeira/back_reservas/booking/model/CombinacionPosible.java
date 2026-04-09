package com.matiasmeira.back_reservas.booking.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "combinaciones_posibles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombinacionPosible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_cancha_id", nullable = false)
    private ProductoCancha productoCancha;

    @ManyToMany
    @JoinTable(
        name = "combinacion_modulos",
        joinColumns = @JoinColumn(name = "combinacion_id"),
        inverseJoinColumns = @JoinColumn(name = "modulo_id")
    )
    private List<ModuloFisico> modulosFisicos;
}