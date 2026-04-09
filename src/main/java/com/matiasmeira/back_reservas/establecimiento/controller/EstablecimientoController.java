package com.matiasmeira.back_reservas.establecimiento.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.matiasmeira.back_reservas.booking.model.Deporte;
import com.matiasmeira.back_reservas.establecimiento.dto.BusquedaEstablecimientoDTO;
import com.matiasmeira.back_reservas.establecimiento.dto.EstablecimientoRequestDTO;
import com.matiasmeira.back_reservas.establecimiento.dto.EstablecimientoResponseDTO;
import com.matiasmeira.back_reservas.establecimiento.service.EstablecimientoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Controlador REST para gestión de establecimientos (clubes, canchas).
 * Proporciona endpoints para búsqueda, detalle y creación de establecimientos.
 */
@RestController
@RequestMapping("/api/establecimientos")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class EstablecimientoController {

    private final EstablecimientoService establecimientoService;

    /**
     * Busca establecimientos con filtros de ubicación, deporte y amenities.
     *
     * @param latitud Latitud para búsqueda cercana (opcional)
     * @param longitud Longitud para búsqueda cercana (opcional)
     * @param radioKm Radio de búsqueda en kilómetros (opcional)
     * @param deporte Deporte a filtrar (opcional)
     * @return Lista de establecimientos que coinciden con los criterios
     */
    @GetMapping
    public ResponseEntity<List<EstablecimientoResponseDTO>> buscarEstablecimientos(
            @RequestParam(required = false) Double latitud,
            @RequestParam(required = false) Double longitud,
            @RequestParam(required = false) Double radioKm,
            @RequestParam(required = false) Deporte deporte) {

        List<EstablecimientoResponseDTO> establecimientos;

        // Si se proporcionan filtros de geolocalización
        if (latitud != null && longitud != null) {
            Double radio = radioKm != null ? radioKm : 10.0; // Radio por defecto 10km
            BusquedaEstablecimientoDTO busqueda = new BusquedaEstablecimientoDTO(
                    latitud, longitud, radio, deporte
            );
            establecimientos = establecimientoService.buscarPorCercaniaYDeporte(busqueda);
        } else if (deporte != null) {
            // Solo filtro por deporte
            establecimientos = establecimientoService.buscarPorDeporte(deporte);
        } else {
            // Todos los establecimientos
            establecimientos = establecimientoService.obtenerTodos();
        }

        return ResponseEntity.ok(establecimientos);
    }

    /**
     * Obtiene el detalle completo de un establecimiento específico.
     * Incluye información de canchas, horarios, amenities y más.
     *
     * @param id Identificador del establecimiento
     * @return Detalle completo del establecimiento
     */
    @GetMapping("/{id}")
    public ResponseEntity<EstablecimientoResponseDTO> obtenerEstablecimiento(@PathVariable Long id) {
        EstablecimientoResponseDTO establecimiento = establecimientoService.obtenerPorId(id);
        return ResponseEntity.ok(establecimiento);
    }

    /**
     * Crea un nuevo establecimiento en el sistema.
     * Solo permitido para usuarios con rol de Dueño/Administrador.
     *
     * @param request Datos del nuevo establecimiento
     * @return Establecimiento creado con su ID generado
     */
    @PostMapping
    public ResponseEntity<EstablecimientoResponseDTO> crearEstablecimiento(
            @Valid @RequestBody EstablecimientoRequestDTO request) {
        EstablecimientoResponseDTO establecimiento = establecimientoService.crear(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(establecimiento);
    }
}
