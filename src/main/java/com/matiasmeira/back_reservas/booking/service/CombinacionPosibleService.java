package com.matiasmeira.back_reservas.booking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matiasmeira.back_reservas.booking.dto.CombinacionPosibleRequestDTO;
import com.matiasmeira.back_reservas.booking.dto.CombinacionPosibleResponseDTO;
import com.matiasmeira.back_reservas.booking.model.CombinacionPosible;
import com.matiasmeira.back_reservas.booking.model.ModuloFisico;
import com.matiasmeira.back_reservas.booking.repository.CombinacionPosibleRepository;
import com.matiasmeira.back_reservas.booking.repository.ModuloFisicoRepository;
import com.matiasmeira.back_reservas.booking.repository.ProductoCanchaRepository;
import com.matiasmeira.back_reservas.exception.EntidadNoEncontradaException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CombinacionPosibleService {

    private final CombinacionPosibleRepository combinacionPosibleRepository;
    private final ProductoCanchaRepository productoCanchaRepository;
    private final ModuloFisicoRepository moduloFisicoRepository;

    @Transactional(readOnly = true)
    public List<CombinacionPosibleResponseDTO> obtenerTodos() {
        return combinacionPosibleRepository.findAll().stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CombinacionPosibleResponseDTO obtenerPorId(Long id) {
        CombinacionPosible combinacion = combinacionPosibleRepository.findById(id)
            .orElseThrow(() -> new EntidadNoEncontradaException("Combinación posible no encontrada"));
        return mapToResponseDTO(combinacion);
    }

    @Transactional
    public CombinacionPosibleResponseDTO crear(CombinacionPosibleRequestDTO request) {
        // Validar que el producto cancha existe
        productoCanchaRepository.findById(request.productoCanchaId())
            .orElseThrow(() -> new EntidadNoEncontradaException("Producto cancha no encontrado"));

        // Validar que todos los módulos físicos existen
        List<ModuloFisico> modulos = request.modulosFisicosIds().stream()
            .map(id -> moduloFisicoRepository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Módulo físico no encontrado: " + id)))
            .collect(Collectors.toList());

        CombinacionPosible combinacion = CombinacionPosible.builder()
            .nombre(request.nombre())
            .productoCancha(productoCanchaRepository.getReferenceById(request.productoCanchaId()))
            .modulosFisicos(modulos)
            .build();

        combinacion = combinacionPosibleRepository.save(combinacion);
        return mapToResponseDTO(combinacion);
    }

    @Transactional
    public CombinacionPosibleResponseDTO actualizar(Long id, CombinacionPosibleRequestDTO request) {
        CombinacionPosible combinacion = combinacionPosibleRepository.findById(id)
            .orElseThrow(() -> new EntidadNoEncontradaException("Combinación posible no encontrada"));

        // Validar que el producto cancha existe
        productoCanchaRepository.findById(request.productoCanchaId())
            .orElseThrow(() -> new EntidadNoEncontradaException("Producto cancha no encontrado"));

        // Validar que todos los módulos físicos existen
        List<ModuloFisico> modulos = request.modulosFisicosIds().stream()
            .map(modId -> moduloFisicoRepository.findById(modId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Módulo físico no encontrado: " + modId)))
            .collect(Collectors.toList());

        combinacion.setNombre(request.nombre());
        combinacion.setProductoCancha(productoCanchaRepository.getReferenceById(request.productoCanchaId()));
        combinacion.setModulosFisicos(modulos);

        combinacion = combinacionPosibleRepository.save(combinacion);
        return mapToResponseDTO(combinacion);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!combinacionPosibleRepository.existsById(id)) {
            throw new EntidadNoEncontradaException("Combinación posible no encontrada");
        }
        combinacionPosibleRepository.deleteById(id);
    }

    private CombinacionPosibleResponseDTO mapToResponseDTO(CombinacionPosible combinacion) {
        return new CombinacionPosibleResponseDTO(
            combinacion.getId(),
            combinacion.getNombre(),
            combinacion.getProductoCancha().getId(),
            combinacion.getProductoCancha().getNombre(),
            combinacion.getModulosFisicos().stream().map(ModuloFisico::getId).collect(Collectors.toList()),
            combinacion.getModulosFisicos().stream().map(ModuloFisico::getNombre).collect(Collectors.toList())
        );
    }
}