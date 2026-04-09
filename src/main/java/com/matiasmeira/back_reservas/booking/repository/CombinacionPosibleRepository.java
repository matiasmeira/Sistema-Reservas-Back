package com.matiasmeira.back_reservas.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matiasmeira.back_reservas.booking.model.CombinacionPosible;

@Repository
public interface CombinacionPosibleRepository extends JpaRepository<CombinacionPosible, Long> {
    List<CombinacionPosible> findByProductoCanchaId(Long id);
}