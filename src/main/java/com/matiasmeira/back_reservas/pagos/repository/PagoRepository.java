package com.matiasmeira.back_reservas.pagos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matiasmeira.back_reservas.pagos.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByMpPreferenceId(String id);
}