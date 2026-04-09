package com.matiasmeira.back_reservas.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matiasmeira.back_reservas.booking.model.ProductoCancha;

@Repository
public interface ProductoCanchaRepository extends JpaRepository<ProductoCancha, Long> {
    List<ProductoCancha> findByEstablecimientoId(Long id);
}