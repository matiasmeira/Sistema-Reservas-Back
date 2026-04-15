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

import com.matiasmeira.back_reservas.booking.dto.ProductoCanchaRequestDTO;
import com.matiasmeira.back_reservas.booking.dto.ProductoCanchaResponseDTO;
import com.matiasmeira.back_reservas.booking.service.ProductoCanchaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/booking/productos-cancha")
@RequiredArgsConstructor
public class ProductoCanchaController {

    private final ProductoCanchaService productoCanchaService;

    @GetMapping
    public ResponseEntity<List<ProductoCanchaResponseDTO>> obtenerTodos() {
        List<ProductoCanchaResponseDTO> productos = productoCanchaService.obtenerTodos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoCanchaResponseDTO> obtenerPorId(@PathVariable Long id) {
        ProductoCanchaResponseDTO producto = productoCanchaService.obtenerPorId(id);
        return ResponseEntity.ok(producto);
    }

    @PostMapping
    @PreAuthorize("hasRole('DUENIO')")
    public ResponseEntity<ProductoCanchaResponseDTO> crear(@Valid @RequestBody ProductoCanchaRequestDTO request) {
        ProductoCanchaResponseDTO producto = productoCanchaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DUENIO')")
    public ResponseEntity<ProductoCanchaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoCanchaRequestDTO request) {
        ProductoCanchaResponseDTO producto = productoCanchaService.actualizar(id, request);
        return ResponseEntity.ok(producto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DUENIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoCanchaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}