package com.matiasmeira.back_reservas.establecimiento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matiasmeira.back_reservas.establecimiento.model.Establecimiento;

@Repository
public interface EstablecimientoRepository extends JpaRepository<Establecimiento, Long> {
    List<Establecimiento> findByUsuarioId(Long usuarioId);
}