package com.matiasmeira.back_reservas.pos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matiasmeira.back_reservas.pos.model.DetalleVenta;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByVentaId(Long id);
}