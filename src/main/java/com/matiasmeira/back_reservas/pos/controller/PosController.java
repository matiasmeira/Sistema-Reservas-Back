package com.matiasmeira.back_reservas.pos.controller;

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

import com.matiasmeira.back_reservas.pos.dto.ProductoDTO;
import com.matiasmeira.back_reservas.pos.dto.ReporteCajaDTO;
import com.matiasmeira.back_reservas.pos.dto.VentaRequestDTO;
import com.matiasmeira.back_reservas.pos.dto.VentaResponseDTO;
import com.matiasmeira.back_reservas.pos.service.POSService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para gestión del sistema POS (Point of Sale).
 * Proporciona endpoints para catálogo de productos, registro de ventas y reportes de caja.
 */
@RestController
@RequestMapping("/api/pos")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class PosController {

    private final POSService posService;

    /**
     * Obtiene el catálogo de productos (buffet, bebidas, etc.) de un establecimiento.
     * Los productos están disponibles para su venta en el POS.
     *
     * @param establecimientoId ID del establecimiento del cual consultar el catálogo
     * @return Lista de productos disponibles del establecimiento
     */
    @GetMapping("/productos/{establecimientoId}")
    public ResponseEntity<List<ProductoDTO>> obtenerCatalogo(@PathVariable Long establecimientoId) {
        List<ProductoDTO> productos = posService.obtenerProductosPorEstablecimiento(establecimientoId);
        return ResponseEntity.ok(productos);
    }

    /**
     * Registra una nueva venta de productos en el POS.
     * Puede ser realizada por Empleados o Dueños del establecimiento.
     * Soporta múltiples métodos de pago (efectivo, tarjeta, transferencia).
     *
     * @param ventaRequest Datos de la venta (establecimiento, productos, monto, método de pago)
     * @return Confirmación de venta con número de transacción
     */
    @PostMapping("/venta")
    public ResponseEntity<VentaResponseDTO> registrarVenta(
            @Valid @RequestBody VentaRequestDTO ventaRequest) {

        VentaResponseDTO venta = posService.crearVenta(ventaRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(venta);
    }

    /**
     * Obtiene un reporte resumen de caja para un día específico.
     * Incluye: total de ventas, número de transacciones y otros indicadores clave.
     *
     * @param establecimientoId ID del establecimiento
     * @param fecha Fecha del reporte (si no se especifica, usa la fecha actual)
     * @return Resumen de caja del día
     */
    @GetMapping("/reporte-diario")
    public ResponseEntity<ReporteCajaDTO> obtenerReporteDiario(
            @RequestParam Long establecimientoId,
            @RequestParam(required = false) LocalDate fecha) {

        LocalDate fechaReporte = fecha != null ? fecha : LocalDate.now();
        ReporteCajaDTO reporte = posService.generarReporteCaja(establecimientoId, fechaReporte);
        return ResponseEntity.ok(reporte);
    }

    /**
     * Obtiene el reporte detallado de ventas en un rango de fechas.
     *
     * @param establecimientoId ID del establecimiento
     * @param fechaInicio Fecha inicial del rango
     * @param fechaFin Fecha final del rango
     * @return Reporte detallado de ventas en el período
     */
    @GetMapping("/reporte-rango")
    public ResponseEntity<List<ReporteCajaDTO>> obtenerReporteRango(
            @RequestParam Long establecimientoId,
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {

        List<ReporteCajaDTO> reportes = posService.generarReporteRango(establecimientoId, fechaInicio, fechaFin);
        return ResponseEntity.ok(reportes);
    }

    /**
     * Obtiene el detalle de una venta específica.
     *
     * @param ventaId ID de la venta
     * @return Datos completos de la venta
     */
    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<VentaResponseDTO> obtenerVenta(@PathVariable Long ventaId) {
        VentaResponseDTO venta = posService.obtenerVentaPorId(ventaId);
        return ResponseEntity.ok(venta);
    }
}
