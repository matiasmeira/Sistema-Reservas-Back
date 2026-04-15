package com.matiasmeira.back_reservas.booking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matiasmeira.back_reservas.booking.dto.CombinacionPosibleRequestDTO;
import com.matiasmeira.back_reservas.booking.dto.CombinacionPosibleResponseDTO;
import com.matiasmeira.back_reservas.booking.service.CombinacionPosibleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/booking/combinaciones-posibles")
@RequiredArgsConstructor
public class CombinacionPosibleController {

    private final CombinacionPosibleService combinacionPosibleService;

    @GetMapping
    public ResponseEntity<List<CombinacionPosibleResponseDTO>> obtenerTodos() {
        List<CombinacionPosibleResponseDTO> combinaciones = combinacionPosibleService.obtenerTodos();
        return ResponseEntity.ok(combinaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CombinacionPosibleResponseDTO> obtenerPorId(@PathVariable Long id) {
        CombinacionPosibleResponseDTO combinacion = combinacionPosibleService.obtenerPorId(id);
        return ResponseEntity.ok(combinacion);
    }

    @PostMapping
    @PreAuthorize("hasRole('DUENIO')")
    public ResponseEntity<CombinacionPosibleResponseDTO> crear(@Valid @RequestBody CombinacionPosibleRequestDTO request) {
        CombinacionPosibleResponseDTO combinacion = combinacionPosibleService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(combinacion);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DUENIO')")
    public ResponseEntity<CombinacionPosibleResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CombinacionPosibleRequestDTO request) {
        CombinacionPosibleResponseDTO combinacion = combinacionPosibleService.actualizar(id, request);
        return ResponseEntity.ok(combinacion);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DUENIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        combinacionPosibleService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}