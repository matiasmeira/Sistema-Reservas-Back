package com.matiasmeira.back_reservas.booking.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matiasmeira.back_reservas.establecimiento.model.Establecimiento;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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