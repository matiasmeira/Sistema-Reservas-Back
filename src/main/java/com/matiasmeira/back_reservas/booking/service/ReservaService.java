package com.matiasmeira.back_reservas.booking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matiasmeira.back_reservas.auth.model.Usuario;
import com.matiasmeira.back_reservas.auth.repository.UsuarioRepository;
import com.matiasmeira.back_reservas.booking.dto.ReservaRequestDTO;
import com.matiasmeira.back_reservas.booking.dto.ReservaResponseDTO;
import com.matiasmeira.back_reservas.booking.model.AsignacionReserva;
import com.matiasmeira.back_reservas.booking.model.CombinacionPosible;
import com.matiasmeira.back_reservas.booking.model.EstadoReserva;
import com.matiasmeira.back_reservas.booking.model.HorarioPrecio;
import com.matiasmeira.back_reservas.booking.model.ModuloFisico;
import com.matiasmeira.back_reservas.booking.model.ProductoCancha;
import com.matiasmeira.back_reservas.booking.model.Reserva;
import com.matiasmeira.back_reservas.booking.repository.AsignacionReservaRepository;
import com.matiasmeira.back_reservas.booking.repository.CombinacionPosibleRepository;
import com.matiasmeira.back_reservas.booking.repository.HorarioPrecioRepository;
import com.matiasmeira.back_reservas.booking.repository.ModuloFisicoRepository;
import com.matiasmeira.back_reservas.booking.repository.ProductoCanchaRepository;
import com.matiasmeira.back_reservas.booking.repository.ReservaRepository;
import com.matiasmeira.back_reservas.exception.EntidadNoEncontradaException;
import com.matiasmeira.back_reservas.exception.ReservaNoDisponibleException;
import com.matiasmeira.back_reservas.pagos.model.EstadoPago;
import com.matiasmeira.back_reservas.pagos.model.Pago;
import com.matiasmeira.back_reservas.pagos.repository.PagoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ProductoCanchaRepository productoCanchaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CombinacionPosibleRepository combinacionPosibleRepository;
    private final HorarioPrecioRepository horarioPrecioRepository;
    private final ModuloFisicoRepository moduloFisicoRepository;
    private final AsignacionReservaRepository asignacionReservaRepository;
    private final PagoRepository pagoRepository;

    @Transactional
    public ReservaResponseDTO crearReserva(ReservaRequestDTO request) {
        // Validar reglas de tiempo del ProductoCancha
        ProductoCancha productoCancha = productoCanchaRepository.findById(request.productoCanchaId())
            .orElseThrow(() -> new EntidadNoEncontradaException("ProductoCancha no encontrado"));

        validarReglasTiempo(productoCancha, request.horaInicio(), request.horaFin());

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
            .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado"));

        // Buscar combinación disponible
        CombinacionPosible combinacion = null;
        Optional<CombinacionPosible> combinacionOpt = buscarCombinacionDisponible(
            productoCancha, request.fechaReserva(), request.horaInicio(), request.horaFin());

        if (combinacionOpt.isPresent()) {
            combinacion = combinacionOpt.get();
        } else if (request.permitirReasignacion()) {
            // Intentar reubicar reservas menores
            combinacionOpt = intentarReasignacion(productoCancha, request.fechaReserva(),
                request.horaInicio(), request.horaFin());
            if (combinacionOpt.isPresent()) {
                combinacion = combinacionOpt.get();
            }
        }

        if (combinacion == null) {
            throw new ReservaNoDisponibleException("No hay combinación disponible para la reserva solicitada");
        }

        // Calcular precio total
        BigDecimal precioTotal = calcularPrecioTotal(productoCancha, request.fechaReserva(),
            request.horaInicio(), request.horaFin());

        // Crear Reserva
        Reserva reserva = Reserva.builder()
            .fechaReserva(request.fechaReserva())
            .horaInicio(request.horaInicio())
            .horaFin(request.horaFin())
            .precioTotal(precioTotal)
            .estado(EstadoReserva.PENDIENTE)
            .usuario(usuario)
            .productoCancha(productoCancha)
            .build();

        reserva = reservaRepository.save(reserva);

        // Crear asignaciones
        final Reserva reservaFinal = reserva; // Variable final para usarla en el lambda
        List<AsignacionReserva> asignaciones = combinacion.getModulosFisicos().stream()
            .map(modulo -> AsignacionReserva.builder()
                .reserva(reservaFinal)
                .moduloFisico(modulo)
                .build())
            .collect(Collectors.toList());

        asignacionReservaRepository.saveAll(asignaciones);

        // Crear Pago inicial
        Pago pago = Pago.builder()
            .montoTotal(precioTotal)
            .comisionPlataforma(BigDecimal.ZERO) // Calcular según lógica
            .montoNetoDuenio(precioTotal)
            .estadoPago(EstadoPago.PENDIENTE)
            .reserva(reservaFinal)
            .build();

        pago = pagoRepository.save(pago);

        return new ReservaResponseDTO(
            reserva.getId(),
            reserva.getFechaReserva(),
            reserva.getHoraInicio(),
            reserva.getHoraFin(),
            reserva.getPrecioTotal(),
            reserva.getEstado(),
            reserva.getUsuario().getId(),
            reserva.getProductoCancha().getId(),
            asignaciones.stream().map(a -> a.getModuloFisico().getId()).collect(Collectors.toList()),
            pago.getId()
        );
    }

    private void validarReglasTiempo(ProductoCancha productoCancha, LocalTime horaInicio, LocalTime horaFin) {
        int duracion = (horaFin.toSecondOfDay() - horaInicio.toSecondOfDay()) / 3600;
        if (duracion < productoCancha.getDuracionMinima()) {
            throw new IllegalArgumentException("Duración mínima no cumplida");
        }
        if ((duracion - productoCancha.getDuracionMinima()) % productoCancha.getIntervaloPaso() != 0) {
            throw new IllegalArgumentException("Intervalo de paso no válido");
        }
    }

    private Optional<CombinacionPosible> buscarCombinacionDisponible(ProductoCancha productoCancha,
            LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        return productoCancha.getCombinacionesPosibles().stream()
            .filter(combo -> estaDisponible(combo, fecha, horaInicio, horaFin))
            .findFirst();
    }

    private boolean estaDisponible(CombinacionPosible combinacion, LocalDate fecha,
            LocalTime horaInicio, LocalTime horaFin) {
        // Buscamos TODOS los módulos activos y libres en ese rango horario para el predio
        List<ModuloFisico> disponibles = moduloFisicoRepository.findAvailableModulos(
            combinacion.getProductoCancha().getEstablecimiento().getId(), fecha, horaInicio, horaFin);
        
        // Una combinación está disponible solo si TODOS sus módulos requeridos están en la lista de libres
        return disponibles.containsAll(combinacion.getModulosFisicos());
    }

    private Optional<CombinacionPosible> intentarReasignacion(ProductoCancha productoCancha,
            LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        // Lógica simplificada: buscar combinaciones con reservas menores y reubicar
        // Implementar lógica compleja aquí
        return Optional.empty(); // Placeholder
    }

    private BigDecimal calcularPrecioTotal(ProductoCancha productoCancha, LocalDate fecha,
            LocalTime horaInicio, LocalTime horaFin) {
        int diaSemana = fecha.getDayOfWeek().getValue();
        int horas = (horaFin.toSecondOfDay() - horaInicio.toSecondOfDay()) / 3600;
        BigDecimal precioHora = horarioPrecioRepository.findPrecioActual(productoCancha.getId(), diaSemana, horaInicio)
            .map(HorarioPrecio::getPrecioHora)
            .orElseThrow(() -> new EntidadNoEncontradaException("Precio no encontrado"));
        return precioHora.multiply(BigDecimal.valueOf(horas));
    }

    /**
     * Obtiene los slots disponibles para una cancha en una fecha específica.
     */
    @Transactional(readOnly = true)
    public List<com.matiasmeira.back_reservas.booking.dto.DisponibilidadDTO> obtenerSlotsDisponibles(
            LocalDate fecha, Long productoCanchaId) {
        
        ProductoCancha productoCancha = productoCanchaRepository.findById(productoCanchaId)
            .orElseThrow(() -> new EntidadNoEncontradaException("ProductoCancha no encontrado"));

        List<com.matiasmeira.back_reservas.booking.dto.DisponibilidadDTO> slots = new java.util.ArrayList<>();
        
        LocalTime horaActual = productoCancha.getEstablecimiento().getHoraApertura();
        LocalTime horaCierre = productoCancha.getEstablecimiento().getHoraCierre();
        int duracionMinutos = productoCancha.getDuracionMinima();
        int pasoMinutos = productoCancha.getIntervaloPaso();
        List<CombinacionPosible> combinaciones = productoCancha.getCombinacionesPosibles();

        while (horaActual.plusMinutes(duracionMinutos).isBefore(horaCierre) || 
            horaActual.plusMinutes(duracionMinutos).equals(horaCierre)) {
            
            // --- LA CORRECCIÓN ESTÁ ACÁ ---
            final LocalTime horaInicioSlot = horaActual; // Copia final para la lambda
            final LocalTime horaFinSlot = horaInicioSlot.plusMinutes(duracionMinutos);
            
            // Ahora usamos 'horaInicioSlot' y 'horaFinSlot' que son final
            boolean hayEspacioFisico = combinaciones.stream()
                .anyMatch(combo -> estaDisponible(combo, fecha, horaInicioSlot, horaFinSlot));

            slots.add(new com.matiasmeira.back_reservas.booking.dto.DisponibilidadDTO(
                horaInicioSlot, 
                horaFinSlot, 
                hayEspacioFisico, 
                hayEspacioFisico ? null : "No hay canchas disponibles"
            ));

            // Avanzamos horaActual normalmente, ya que no se usa dentro de la lambda
            horaActual = horaActual.plusMinutes(pasoMinutos);
        }
        
        return slots;
    }

    /**
     * Obtiene todas las reservas de un usuario.
     */
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerReservasPorUsuario(Long usuarioId) {
        usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado"));

        return reservaRepository.findAll().stream()
            .filter(r -> r.getUsuario().getId().equals(usuarioId))
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene una reserva por su ID.
     */
    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerPorId(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new EntidadNoEncontradaException("Reserva no encontrada"));
        return mapToResponse(reserva);
    }

    /**
     * Cancela una reserva existente.
     */
    @Transactional
    public void cancelarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new EntidadNoEncontradaException("Reserva no encontrada"));

        if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException("No se puede cancelar una reserva confirmada");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    /**
     * Mapea una entidad Reserva a su DTO de respuesta.
     */
    private ReservaResponseDTO mapToResponse(Reserva reserva) {
        List<Long> modulosIds = asignacionReservaRepository.findAll().stream()
            .filter(a -> a.getReserva().getId().equals(reserva.getId()))
            .map(a -> a.getModuloFisico().getId())
            .collect(Collectors.toList());

        Long pagoId = pagoRepository.findAll().stream()
            .filter(p -> p.getReserva().getId().equals(reserva.getId()))
            .map(Pago::getId)
            .findFirst()
            .orElse(null);

        return new ReservaResponseDTO(
            reserva.getId(),
            reserva.getFechaReserva(),
            reserva.getHoraInicio(),
            reserva.getHoraFin(),
            reserva.getPrecioTotal(),
            reserva.getEstado(),
            reserva.getUsuario().getId(),
            reserva.getProductoCancha().getId(),
            modulosIds,
            pagoId
        );
    }
}