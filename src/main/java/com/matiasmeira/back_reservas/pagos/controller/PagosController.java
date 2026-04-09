package com.matiasmeira.back_reservas.pagos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matiasmeira.back_reservas.pagos.dto.MpPaymentDTO;
import com.matiasmeira.back_reservas.pagos.service.PagoService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para gestión de pagos y webhooks de Mercado Pago.
 * Endpoint público para recibir notificaciones IPN de Mercado Pago.
 */
@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class PagosController {

    private final PagoService pagoService;

    /**
     * Endpoint webhook para recibir notificaciones de Mercado Pago.
     * Este endpoint es PÚBLICO y debe estar disponible sin autenticación.
     * Recibe notificaciones IPN (Instant Payment Notification) cuando el estado del pago cambia.
     *
     * El flujo es:
     * 1. Usuario realiza pago en Mercado Pago
     * 2. Mercado Pago notifica cambio de estado a este endpoint
     * 3. Se actualiza el estado del pago y de la reserva asociada
     * 4. Si aprobado, se confirma la reserva
     *
     * @param paymentDTO Datos del pago enviados por Mercado Pago
     * @return Confirmación de recepción (200 OK)
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> procesarWebhookMercadoPago(
            @RequestBody MpPaymentDTO paymentDTO) {

        pagoService.procesarWebhook(paymentDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Retenta el procesamiento de un pago que falló.
     *
     * @param pagoId ID del pago a reintentar
     * @return Confirmación del reintento
     */
    @PostMapping("/{pagoId}/reintentar")
    public ResponseEntity<Void> reintentarPago(@PathVariable Long pagoId) {
        pagoService.reintentarPago(pagoId);
        return ResponseEntity.ok().build();
    }
}
