package com.matiasmeira.back_reservas.pagos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matiasmeira.back_reservas.booking.model.EstadoReserva;
import com.matiasmeira.back_reservas.booking.repository.ReservaRepository;
import com.matiasmeira.back_reservas.exception.EntidadNoEncontradaException;
import com.matiasmeira.back_reservas.pagos.dto.MpPaymentDTO;
import com.matiasmeira.back_reservas.pagos.model.EstadoPago;
import com.matiasmeira.back_reservas.pagos.model.Pago;
import com.matiasmeira.back_reservas.pagos.repository.PagoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;

    @Transactional
    public void procesarWebhook(MpPaymentDTO paymentDTO) {
        // Asumir que paymentDTO incluye reservaId o buscar por paymentId
        // Para simplicidad, buscar pago por reservaId (necesitaría agregar reservaId a DTO)
        // Placeholder: buscar por paymentId si existe
        Pago pago = pagoRepository.findAll().stream()
            .filter(p -> paymentDTO.paymentId().equals(p.getMpPaymentId()))
            .findFirst()
            .orElseThrow(() -> new EntidadNoEncontradaException("Pago no encontrado"));

        // Actualizar estado del pago
        EstadoPago nuevoEstado = mapStatusToEstadoPago(paymentDTO.status());
        pago.setEstadoPago(nuevoEstado);
        pago.setMpPaymentId(paymentDTO.paymentId());

        pagoRepository.save(pago);

        // Si aprobado, confirmar reserva
        if (nuevoEstado == EstadoPago.APROBADO) {
            pago.getReserva().setEstado(EstadoReserva.CONFIRMADA);
            reservaRepository.save(pago.getReserva());
        }
    }

    private EstadoPago mapStatusToEstadoPago(String status) {
        return switch (status.toLowerCase()) {
            case "approved" -> EstadoPago.APROBADO;
            case "rejected" -> EstadoPago.RECHAZADO;
            default -> EstadoPago.PENDIENTE;
        };
    }

    // Mock para integración con Mercado Pago
    public String crearPreferenciaPago(Long reservaId) {
        // Lógica mock
        return "preference_id_mock";
    }

    /**
     * Reintenta procesar un pago que ha fallado.
     */
    @Transactional
    public void reintentarPago(Long pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
            .orElseThrow(() -> new EntidadNoEncontradaException("Pago no encontrado"));

        if (pago.getEstadoPago() != EstadoPago.RECHAZADO) {
            throw new IllegalStateException("Solo se pueden reintentar pagos rechazados");
        }

        // Restablecer estado a pendiente para reintentar
        pago.setEstadoPago(EstadoPago.PENDIENTE);
        pagoRepository.save(pago);
    }
}