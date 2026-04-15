package com.matiasmeira.back_reservas.booking.controller;

import java.time.LocalDate;
import java.util.List;

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

import com.matiasmeira.back_reservas.booking.dto.DisponibilidadDTO;
import com.matiasmeira.back_reservas.booking.dto.ReservaRequestDTO;
import com.matiasmeira.back_reservas.booking.dto.ReservaResponseDTO;
import com.matiasmeira.back_reservas.booking.service.ReservaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para gestión de reservas y disponibilidad de canchas.
 * Proporciona endpoints para crear reservas, consultar disponibilidad e historial.
 */
@RestController
@RequestMapping("/booking")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class BookingController {

    private final ReservaService reservaService;

    /**
     * Obtiene los slots de tiempo disponibles para una cancha en una fecha específica.
     * Calcula automáticamente la lógica de módulos modulares.
     *
     * @param fecha Fecha de la reserva deseada
     * @param productoCanchaId ID del producto/cancha
     * @return Lista de slots disponibles
     */
    @GetMapping("/disponibilidad")
    public ResponseEntity<List<DisponibilidadDTO>> obtenerDisponibilidad(
            @RequestParam LocalDate fecha,
            @RequestParam Long productoCanchaId) {

        List<DisponibilidadDTO> disponibilidad = reservaService.obtenerSlotsDisponibles(fecha, productoCanchaId);
        return ResponseEntity.ok(disponibilidad);
    }

    /**
     * Crea una nueva reserva para una cancha en una fecha y hora específica.
     * Si la reserva es exitosa, devuelve el link de pago de Mercado Pago.
     *
     * @param request Datos de la nueva reserva a crear
     * @return Datos de la reserva creada incluyendo ID de la transacción y link de pago
     */
    @PostMapping("/reservar")
    public ResponseEntity<ReservaResponseDTO> crearReserva(
            @Valid @RequestBody ReservaRequestDTO request) {

        ReservaResponseDTO reserva = reservaService.crearReserva(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reserva);
    }

    /**
     * Obtiene el historial de reservas del cliente autenticado.
     * Incluye reservas confirmadas, pendientes y canceladas.
     *
     * @param usuarioId ID del usuario propietario de las reservas
     * @return Lista de todas las reservas del usuario
     */
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerMisReservas(
            @RequestParam Long usuarioId) {

        List<ReservaResponseDTO> reservas = reservaService.obtenerReservasPorUsuario(usuarioId);
        return ResponseEntity.ok(reservas);
    }

    /**
     * Obtiene el detalle de una reserva específica.
     *
     * @param reservaId ID de la reserva a consultar
     * @return Datos completos de la reserva
     */
    @GetMapping("/{reservaId}")
    public ResponseEntity<ReservaResponseDTO> obtenerReserva(@PathVariable Long reservaId) {
        ReservaResponseDTO reserva = reservaService.obtenerPorId(reservaId);
        return ResponseEntity.ok(reserva);
    }

    /**
     * Cancela una reserva existente.
     * Solo permite cancelar reservas que no han sido confirmadas del pago.
     *
     * @param reservaId ID de la reserva a cancelar
     * @return Confirmación de cancelación
     */
    @PostMapping("/{reservaId}/cancelar")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long reservaId) {
        reservaService.cancelarReserva(reservaId);
        return ResponseEntity.noContent().build();
    }
}
