package com.matiasmeira.back_reservas.booking.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.matiasmeira.back_reservas.booking.model.HorarioPrecio;

@Repository
public interface HorarioPrecioRepository extends JpaRepository<HorarioPrecio, Long> {
    List<HorarioPrecio> findByProductoCanchaId(Long productoCanchaId);

    @Query("SELECT hp FROM HorarioPrecio hp WHERE hp.productoCancha.id = :productoCanchaId AND hp.diaSemana = :diaSemana AND :hora BETWEEN hp.horaInicio AND hp.horaFin")
    Optional<HorarioPrecio> findPrecioActual(@Param("productoCanchaId") Long productoCanchaId, @Param("diaSemana") int diaSemana, @Param("hora") LocalTime hora);
}