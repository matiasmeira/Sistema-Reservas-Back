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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.matiasmeira.back_reservas.booking.dto.HorarioPrecioRequestDTO;
import com.matiasmeira.back_reservas.booking.dto.HorarioPrecioResponseDTO;
import com.matiasmeira.back_reservas.booking.service.HorarioPrecioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/booking/horarios-precio")
@RequiredArgsConstructor
public class HorarioPrecioController {

    private final HorarioPrecioService horarioPrecioService;

    @GetMapping
    public ResponseEntity<List<HorarioPrecioResponseDTO>> obtenerTodos() {
        List<HorarioPrecioResponseDTO> horarios = horarioPrecioService.obtenerTodos();
        return ResponseEntity.ok(horarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioPrecioResponseDTO> obtenerPorId(@PathVariable Long id) {
        HorarioPrecioResponseDTO horario = horarioPrecioService.obtenerPorId(id);
        return ResponseEntity.ok(horario);
    }

    @GetMapping("/producto-cancha")
    public ResponseEntity<List<HorarioPrecioResponseDTO>> obtenerPorProductoCancha(@RequestParam Long productoCanchaId) {
        List<HorarioPrecioResponseDTO> horarios = horarioPrecioService.obtenerPorProductoCancha(productoCanchaId);
        return ResponseEntity.ok(horarios);
    }

    @PostMapping
    @PreAuthorize("hasRole('DUENIO')")
    public ResponseEntity<HorarioPrecioResponseDTO> crear(@Valid @RequestBody HorarioPrecioRequestDTO request) {
        HorarioPrecioResponseDTO horario = horarioPrecioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(horario);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DUENIO')")
    public ResponseEntity<HorarioPrecioResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody HorarioPrecioRequestDTO request) {
        HorarioPrecioResponseDTO horario = horarioPrecioService.actualizar(id, request);
        return ResponseEntity.ok(horario);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DUENIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        horarioPrecioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}