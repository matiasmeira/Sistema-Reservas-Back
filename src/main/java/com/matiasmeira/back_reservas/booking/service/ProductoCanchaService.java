package com.matiasmeira.back_reservas.booking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matiasmeira.back_reservas.booking.dto.ProductoCanchaRequestDTO;
import com.matiasmeira.back_reservas.booking.dto.ProductoCanchaResponseDTO;
import com.matiasmeira.back_reservas.booking.model.ProductoCancha;
import com.matiasmeira.back_reservas.booking.repository.ProductoCanchaRepository;
import com.matiasmeira.back_reservas.establecimiento.repository.EstablecimientoRepository;
import com.matiasmeira.back_reservas.exception.EntidadNoEncontradaException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoCanchaService {

    private final ProductoCanchaRepository productoCanchaRepository;
    private final EstablecimientoRepository establecimientoRepository;

    @Transactional(readOnly = true)
    public List<ProductoCanchaResponseDTO> obtenerTodos() {
        return productoCanchaRepository.findAll().stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoCanchaResponseDTO obtenerPorId(Long id) {
        ProductoCancha producto = productoCanchaRepository.findById(id)
            .orElseThrow(() -> new EntidadNoEncontradaException("Producto cancha no encontrado"));
        return mapToResponseDTO(producto);
    }

    @Transactional
    public ProductoCanchaResponseDTO crear(ProductoCanchaRequestDTO request) {
        // Validar que el establecimiento existe
        establecimientoRepository.findById(request.establecimientoId())
            .orElseThrow(() -> new EntidadNoEncontradaException("Establecimiento no encontrado"));

        ProductoCancha producto = ProductoCancha.builder()
            .nombre(request.nombre())
            .deporte(request.deporte())
            .superficie(request.superficie())
            .modulosNecesarios(request.modulosNecesarios())
            .duracionMinima(request.duracionMinima())
            .intervaloPaso(request.intervaloPaso())
            .establecimiento(establecimientoRepository.getReferenceById(request.establecimientoId()))
            .build();

        producto = productoCanchaRepository.save(producto);
        return mapToResponseDTO(producto);
    }

    @Transactional
    public ProductoCanchaResponseDTO actualizar(Long id, ProductoCanchaRequestDTO request) {
        ProductoCancha producto = productoCanchaRepository.findById(id)
            .orElseThrow(() -> new EntidadNoEncontradaException("Producto cancha no encontrado"));

        // Validar que el establecimiento existe si se cambia
        establecimientoRepository.findById(request.establecimientoId())
            .orElseThrow(() -> new EntidadNoEncontradaException("Establecimiento no encontrado"));

        producto.setNombre(request.nombre());
        producto.setDeporte(request.deporte());
        producto.setSuperficie(request.superficie());
        producto.setModulosNecesarios(request.modulosNecesarios());
        producto.setDuracionMinima(request.duracionMinima());
        producto.setIntervaloPaso(request.intervaloPaso());
        producto.setEstablecimiento(establecimientoRepository.getReferenceById(request.establecimientoId()));

        producto = productoCanchaRepository.save(producto);
        return mapToResponseDTO(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!productoCanchaRepository.existsById(id)) {
            throw new EntidadNoEncontradaException("Producto cancha no encontrado");
        }
        productoCanchaRepository.deleteById(id);
    }

    private ProductoCanchaResponseDTO mapToResponseDTO(ProductoCancha producto) {
        return new ProductoCanchaResponseDTO(
            producto.getId(),
            producto.getNombre(),
            producto.getDeporte(),
            producto.getSuperficie(),
            producto.getModulosNecesarios(),
            producto.getDuracionMinima(),
            producto.getIntervaloPaso(),
            producto.getEstablecimiento().getId(),
            producto.getEstablecimiento().getNombre()
        );
    }
}