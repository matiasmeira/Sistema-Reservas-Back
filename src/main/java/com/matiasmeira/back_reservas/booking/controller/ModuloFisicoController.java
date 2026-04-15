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

import com.matiasmeira.back_reservas.booking.dto.ModuloFisicoRequestDTO;
import com.matiasmeira.back_reservas.booking.dto.ModuloFisicoResponseDTO;
import com.matiasmeira.back_reservas.booking.service.ModuloFisicoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/booking/modulos-fisicos")
@RequiredArgsConstructor
public class ModuloFisicoController {

    private final ModuloFisicoService moduloFisicoService;

    @GetMapping
    public ResponseEntity<List<ModuloFisicoResponseDTO>> obtenerTodos() {
        List<ModuloFisicoResponseDTO> modulos = moduloFisicoService.obtenerTodos();
        return ResponseEntity.ok(modulos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuloFisicoResponseDTO> obtenerPorId(@PathVariable Long id) {
        ModuloFisicoResponseDTO modulo = moduloFisicoService.obtenerPorId(id);
        return ResponseEntity.ok(modulo);
    }

    @PostMapping
    @PreAuthorize("hasRole('DUENIO')")
    public ResponseEntity<ModuloFisicoResponseDTO> crear(@Valid @RequestBody ModuloFisicoRequestDTO request) {
        ModuloFisicoResponseDTO modulo = moduloFisicoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(modulo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DUENIO')")
    public ResponseEntity<ModuloFisicoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ModuloFisicoRequestDTO request) {
        ModuloFisicoResponseDTO modulo = moduloFisicoService.actualizar(id, request);
        return ResponseEntity.ok(modulo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DUENIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        moduloFisicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}