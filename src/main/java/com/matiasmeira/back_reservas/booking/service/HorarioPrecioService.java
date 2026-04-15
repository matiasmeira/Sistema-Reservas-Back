package com.matiasmeira.back_reservas.booking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matiasmeira.back_reservas.booking.dto.HorarioPrecioRequestDTO;
import com.matiasmeira.back_reservas.booking.dto.HorarioPrecioResponseDTO;
import com.matiasmeira.back_reservas.booking.model.HorarioPrecio;
import com.matiasmeira.back_reservas.booking.repository.HorarioPrecioRepository;
import com.matiasmeira.back_reservas.booking.repository.ProductoCanchaRepository;
import com.matiasmeira.back_reservas.exception.EntidadNoEncontradaException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HorarioPrecioService {

    private final HorarioPrecioRepository horarioPrecioRepository;
    private final ProductoCanchaRepository productoCanchaRepository;

    @Transactional(readOnly = true)
    public List<HorarioPrecioResponseDTO> obtenerTodos() {
        return horarioPrecioRepository.findAll().stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HorarioPrecioResponseDTO obtenerPorId(Long id) {
        HorarioPrecio horario = horarioPrecioRepository.findById(id)
            .orElseThrow(() -> new EntidadNoEncontradaException("Horario precio no encontrado"));
        return mapToResponseDTO(horario);
    }

    @Transactional(readOnly = true)
    public List<HorarioPrecioResponseDTO> obtenerPorProductoCancha(Long productoCanchaId) {
        return horarioPrecioRepository.findByProductoCanchaId(productoCanchaId).stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public HorarioPrecioResponseDTO crear(HorarioPrecioRequestDTO request) {
        // Validar que el producto cancha existe
        productoCanchaRepository.findById(request.productoCanchaId())
            .orElseThrow(() -> new EntidadNoEncontradaException("Producto cancha no encontrado"));

        HorarioPrecio horario = HorarioPrecio.builder()
            .diaSemana(request.diaSemana())
            .horaInicio(request.horaInicio())
            .horaFin(request.horaFin())
            .precioHora(request.precioHora())
            .productoCancha(productoCanchaRepository.getReferenceById(request.productoCanchaId()))
            .build();

        horario = horarioPrecioRepository.save(horario);
        return mapToResponseDTO(horario);
    }

    @Transactional
    public HorarioPrecioResponseDTO actualizar(Long id, HorarioPrecioRequestDTO request) {
        HorarioPrecio horario = horarioPrecioRepository.findById(id)
            .orElseThrow(() -> new EntidadNoEncontradaException("Horario precio no encontrado"));

        // Validar que el producto cancha existe
        productoCanchaRepository.findById(request.productoCanchaId())
            .orElseThrow(() -> new EntidadNoEncontradaException("Producto cancha no encontrado"));

        horario.setDiaSemana(request.diaSemana());
        horario.setHoraInicio(request.horaInicio());
        horario.setHoraFin(request.horaFin());
        horario.setPrecioHora(request.precioHora());
        horario.setProductoCancha(productoCanchaRepository.getReferenceById(request.productoCanchaId()));

        horario = horarioPrecioRepository.save(horario);
        return mapToResponseDTO(horario);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!horarioPrecioRepository.existsById(id)) {
            throw new EntidadNoEncontradaException("Horario precio no encontrado");
        }
        horarioPrecioRepository.deleteById(id);
    }

    private HorarioPrecioResponseDTO mapToResponseDTO(HorarioPrecio horario) {
        return new HorarioPrecioResponseDTO(
            horario.getId(),
            horario.getDiaSemana(),
            horario.getHoraInicio(),
            horario.getHoraFin(),
            horario.getPrecioHora(),
            horario.getProductoCancha().getId(),
            horario.getProductoCancha().getNombre()
        );
    }
}