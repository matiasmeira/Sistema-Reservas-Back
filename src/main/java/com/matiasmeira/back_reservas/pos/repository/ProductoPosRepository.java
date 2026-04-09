package com.matiasmeira.back_reservas.pos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matiasmeira.back_reservas.pos.model.ProductoPos;

@Repository
public interface ProductoPosRepository extends JpaRepository<ProductoPos, Long> {
    List<ProductoPos> findByEstablecimientoIdAndActivoTrue(Long id);
}