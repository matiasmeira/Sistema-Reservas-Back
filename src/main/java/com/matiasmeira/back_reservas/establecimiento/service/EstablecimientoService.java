package com.matiasmeira.back_reservas.establecimiento.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matiasmeira.back_reservas.auth.model.Usuario;
import com.matiasmeira.back_reservas.auth.repository.UsuarioRepository;
import com.matiasmeira.back_reservas.booking.model.Deporte;
import com.matiasmeira.back_reservas.establecimiento.dto.BusquedaEstablecimientoDTO;
import com.matiasmeira.back_reservas.establecimiento.dto.EstablecimientoRequestDTO;
import com.matiasmeira.back_reservas.establecimiento.dto.EstablecimientoResponseDTO;
import com.matiasmeira.back_reservas.establecimiento.model.Amenity;
import com.matiasmeira.back_reservas.establecimiento.model.Establecimiento;
import com.matiasmeira.back_reservas.establecimiento.repository.AmenityRepository;
import com.matiasmeira.back_reservas.establecimiento.repository.EstablecimientoRepository;
import com.matiasmeira.back_reservas.exception.EntidadNoEncontradaException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EstablecimientoService {

    private final EstablecimientoRepository establecimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AmenityRepository amenityRepository;

    @Transactional
    public EstablecimientoResponseDTO crearEstablecimiento(EstablecimientoRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
            .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado"));

        List<Amenity> amenities = request.amenityIds().stream()
            .map(id -> amenityRepository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Amenity no encontrado: " + id)))
            .collect(Collectors.toList());

        Establecimiento establecimiento = Establecimiento.builder()
            .nombre(request.nombre())
            .direccion(request.direccion())
            .latitud(request.latitud())
            .longitud(request.longitud())
            .horaApertura(request.horaApertura())
            .horaCierre(request.horaCierre())
            .tipoPlan(request.tipoPlan())
            .permitirReasignacion(request.permitirReasignacion())
            .requiereSena(request.requiereSena())
            .porcentajeSena(request.porcentajeSena())
            .estado(request.estado())
            .usuario(usuario)
            .amenities(amenities)
            .build();

        establecimiento = establecimientoRepository.save(establecimiento);

        return mapToResponse(establecimiento);
    }

    @Transactional(readOnly = true)
    public List<EstablecimientoResponseDTO> buscarEstablecimientos(BusquedaEstablecimientoDTO busqueda) {
        return establecimientoRepository.findAll().stream()
            .filter(est -> estaEnRadio(est, busqueda.latitud(), busqueda.longitud(), busqueda.radioKm()))
            .filter(est -> tieneDeporte(est, busqueda.deporte()))
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private boolean estaEnRadio(Establecimiento est, Double lat, Double lng, Double radioKm) {
        if (est.getLatitud() == null || est.getLongitud() == null) return false;
        // Calcular distancia simple (aproximada)
        double dist = Math.sqrt(Math.pow(est.getLatitud() - lat, 2) + Math.pow(est.getLongitud() - lng, 2)) * 111; // km approx
        return dist <= radioKm;
    }

    private boolean tieneDeporte(Establecimiento est, Deporte deporte) {
        return est.getProductosCancha().stream()
            .anyMatch(pc -> pc.getDeporte() == deporte);
    }

    @Transactional(readOnly = true)
    public Optional<EstablecimientoResponseDTO> obtenerEstablecimiento(Long id) {
        return establecimientoRepository.findById(id).map(this::mapToResponse);
    }

    @Transactional
    public EstablecimientoResponseDTO actualizarEstablecimiento(Long id, EstablecimientoRequestDTO request) {
        Establecimiento establecimiento = establecimientoRepository.findById(id)
            .orElseThrow(() -> new EntidadNoEncontradaException("Establecimiento no encontrado"));

        // Actualizar campos
        establecimiento.setNombre(request.nombre());
        establecimiento.setDireccion(request.direccion());
        establecimiento.setLatitud(request.latitud());
        establecimiento.setLongitud(request.longitud());
        establecimiento.setHoraApertura(request.horaApertura());
        establecimiento.setHoraCierre(request.horaCierre());
        establecimiento.setTipoPlan(request.tipoPlan());
        establecimiento.setPermitirReasignacion(request.permitirReasignacion());
        establecimiento.setRequiereSena(request.requiereSena());
        establecimiento.setPorcentajeSena(request.porcentajeSena());
        establecimiento.setEstado(request.estado());

        List<Amenity> amenities = request.amenityIds().stream()
            .map(amenityId -> amenityRepository.findById(amenityId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Amenity no encontrado: " + amenityId)))
            .collect(Collectors.toList());
        establecimiento.setAmenities(amenities);

        establecimiento = establecimientoRepository.save(establecimiento);

        return mapToResponse(establecimiento);
    }

    @Transactional
    public void eliminarEstablecimiento(Long id) {
        if (!establecimientoRepository.existsById(id)) {
            throw new EntidadNoEncontradaException("Establecimiento no encontrado");
        }
        establecimientoRepository.deleteById(id);
    }

    /**
     * Busca establecimientos por cercanía geográfica y deporte.
     */
    @Transactional(readOnly = true)
    public List<EstablecimientoResponseDTO> buscarPorCercaniaYDeporte(BusquedaEstablecimientoDTO busqueda) {
        return establecimientoRepository.findAll().stream()
            .filter(est -> estaEnRadio(est, busqueda.latitud(), busqueda.longitud(), busqueda.radioKm()))
            .filter(est -> busqueda.deporte() == null || tieneDeporte(est, busqueda.deporte()))
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Busca establecimientos por deporte específico.
     */
    @Transactional(readOnly = true)
    public List<EstablecimientoResponseDTO> buscarPorDeporte(Deporte deporte) {
        return establecimientoRepository.findAll().stream()
            .filter(est -> tieneDeporte(est, deporte))
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los establecimientos.
     */
    @Transactional(readOnly = true)
    public List<EstablecimientoResponseDTO> obtenerTodos() {
        return establecimientoRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene un establecimiento por ID.
     */
    @Transactional(readOnly = true)
    public EstablecimientoResponseDTO obtenerPorId(Long id) {
        Establecimiento establecimiento = establecimientoRepository.findById(id)
            .orElseThrow(() -> new EntidadNoEncontradaException("Establecimiento no encontrado"));
        return mapToResponse(establecimiento);
    }

    /**
     * Crea un nuevo establecimiento.
     */
    @Transactional
    public EstablecimientoResponseDTO crear(EstablecimientoRequestDTO request) {
        return crearEstablecimiento(request);
    }

    private EstablecimientoResponseDTO mapToResponse(Establecimiento establecimiento) {
        return new EstablecimientoResponseDTO(
            establecimiento.getId(),
            establecimiento.getNombre(),
            establecimiento.getDireccion(),
            establecimiento.getLatitud(),
            establecimiento.getLongitud(),
            establecimiento.getHoraApertura(),
            establecimiento.getHoraCierre(),
            establecimiento.getTipoPlan(),
            establecimiento.isPermitirReasignacion(),
            establecimiento.isRequiereSena(),
            establecimiento.getPorcentajeSena(),
            establecimiento.getEstado(),
            establecimiento.getUsuario().getId(),
            establecimiento.getAmenities().stream().map(Amenity::getId).collect(Collectors.toList())
        );
    }
}