package com.matiasmeira.back_reservas.booking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matiasmeira.back_reservas.booking.dto.ModuloFisicoRequestDTO;
import com.matiasmeira.back_reservas.booking.dto.ModuloFisicoResponseDTO;
import com.matiasmeira.back_reservas.booking.model.ModuloFisico;
import com.matiasmeira.back_reservas.booking.repository.ModuloFisicoRepository;
import com.matiasmeira.back_reservas.establecimiento.repository.EstablecimientoRepository;
import com.matiasmeira.back_reservas.exception.EntidadNoEncontradaException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModuloFisicoService {

    private final ModuloFisicoRepository moduloFisicoRepository;
    private final EstablecimientoRepository establecimientoRepository;

    @Transactional(readOnly = true)
    public List<ModuloFisicoResponseDTO> obtenerTodos() {
        return moduloFisicoRepository.findAll().stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ModuloFisicoResponseDTO obtenerPorId(Long id) {
        ModuloFisico modulo = moduloFisicoRepository.findById(id)
            .orElseThrow(() -> new EntidadNoEncontradaException("Módulo físico no encontrado"));
        return mapToResponseDTO(modulo);
    }

    @Transactional
    public ModuloFisicoResponseDTO crear(ModuloFisicoRequestDTO request) {
        // Validar que el establecimiento existe
        establecimientoRepository.findById(request.establecimientoId())
            .orElseThrow(() -> new EntidadNoEncontradaException("Establecimiento no encontrado"));

        ModuloFisico modulo = ModuloFisico.builder()
            .nombre(request.nombre())
            .estado(request.estado())
            .establecimiento(establecimientoRepository.getReferenceById(request.establecimientoId()))
            .build();

        modulo = moduloFisicoRepository.save(modulo);
        return mapToResponseDTO(modulo);
    }

    @Transactional
    public ModuloFisicoResponseDTO actualizar(Long id, ModuloFisicoRequestDTO request) {
        ModuloFisico modulo = moduloFisicoRepository.findById(id)
            .orElseThrow(() -> new EntidadNoEncontradaException("Módulo físico no encontrado"));

        // Validar que el establecimiento existe si se cambia
        establecimientoRepository.findById(request.establecimientoId())
            .orElseThrow(() -> new EntidadNoEncontradaException("Establecimiento no encontrado"));

        modulo.setNombre(request.nombre());
        modulo.setEstado(request.estado());
        modulo.setEstablecimiento(establecimientoRepository.getReferenceById(request.establecimientoId()));

        modulo = moduloFisicoRepository.save(modulo);
        return mapToResponseDTO(modulo);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!moduloFisicoRepository.existsById(id)) {
            throw new EntidadNoEncontradaException("Módulo físico no encontrado");
        }
        moduloFisicoRepository.deleteById(id);
    }

    private ModuloFisicoResponseDTO mapToResponseDTO(ModuloFisico modulo) {
        return new ModuloFisicoResponseDTO(
            modulo.getId(),
            modulo.getNombre(),
            modulo.getEstado(),
            modulo.getEstablecimiento().getId(),
            modulo.getEstablecimiento().getNombre()
        );
    }
}