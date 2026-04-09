package com.matiasmeira.back_reservas.pos.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matiasmeira.back_reservas.auth.model.Usuario;
import com.matiasmeira.back_reservas.auth.repository.UsuarioRepository;
import com.matiasmeira.back_reservas.establecimiento.model.Establecimiento;
import com.matiasmeira.back_reservas.establecimiento.repository.EstablecimientoRepository;
import com.matiasmeira.back_reservas.exception.EntidadNoEncontradaException;
import com.matiasmeira.back_reservas.pos.dto.DetalleVentaRequestDTO;
import com.matiasmeira.back_reservas.pos.dto.ReporteCajaDTO;
import com.matiasmeira.back_reservas.pos.dto.VentaRequestDTO;
import com.matiasmeira.back_reservas.pos.model.DetalleVenta;
import com.matiasmeira.back_reservas.pos.model.ProductoPos;
import com.matiasmeira.back_reservas.pos.model.Venta;
import com.matiasmeira.back_reservas.pos.repository.DetalleVentaRepository;
import com.matiasmeira.back_reservas.pos.repository.ProductoPosRepository;
import com.matiasmeira.back_reservas.pos.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class POSService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoPosRepository productoPosRepository;
    private final EstablecimientoRepository establecimientoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Venta registrarVenta(VentaRequestDTO request) {
        Establecimiento establecimiento = establecimientoRepository.findById(request.establecimientoId())
            .orElseThrow(() -> new EntidadNoEncontradaException("Establecimiento no encontrado"));

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
            .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado"));

        BigDecimal total = request.detalles().stream()
            .map(detalle -> calcularSubtotal(detalle))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Venta venta = Venta.builder()
            .total(total)
            .fechaHora(LocalDateTime.now())
            .metodoPago(request.metodoPago())
            .establecimiento(establecimiento)
            .usuario(usuario)
            .build();

        venta = ventaRepository.save(venta);

        final Venta ventaFinal = venta; // Variable final para usarla en el lambda
        List<DetalleVenta> detalles = request.detalles().stream()
            .map(detalleReq -> {
                ProductoPos producto = productoPosRepository.findById(detalleReq.productoPosId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado"));
                BigDecimal precioHistorico = producto.getPrecio(); // Usar precio histórico
                return DetalleVenta.builder()
                    .venta(ventaFinal)
                    .productoPos(producto)
                    .cantidad(detalleReq.cantidad())
                    .precioUnitario(precioHistorico)
                    .build();
            })
            .collect(Collectors.toList());

        detalleVentaRepository.saveAll(detalles);

        return venta;
    }

    @Transactional(readOnly = true)
    public ReporteCajaDTO obtenerReporteCajaDiario(Long establecimientoId, LocalDate fecha) {
        List<Venta> ventas = ventaRepository.findAll().stream()
            .filter(v -> v.getEstablecimiento().getId().equals(establecimientoId))
            .filter(v -> v.getFechaHora().toLocalDate().equals(fecha))
            .collect(Collectors.toList());
        BigDecimal total = ventas.stream()
            .map(Venta::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ReporteCajaDTO(fecha, total, ventas.size());
    }

    private BigDecimal calcularSubtotal(DetalleVentaRequestDTO detalle) {
        ProductoPos producto = productoPosRepository.findById(detalle.productoPosId())
            .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado"));
        return producto.getPrecio().multiply(BigDecimal.valueOf(detalle.cantidad()));
    }

    /**
     * Obtiene los productos de un establecimiento.
     */
    @Transactional(readOnly = true)
    public List<com.matiasmeira.back_reservas.pos.dto.ProductoDTO> obtenerProductosPorEstablecimiento(
            Long establecimientoId) {
        Establecimiento establecimiento = establecimientoRepository.findById(establecimientoId)
            .orElseThrow(() -> new EntidadNoEncontradaException("Establecimiento no encontrado"));

        return productoPosRepository.findAll().stream()
            .filter(p -> p.getEstablecimiento().getId().equals(establecimientoId))
            .map(p -> new com.matiasmeira.back_reservas.pos.dto.ProductoDTO(
                p.getId(), p.getNombre(), p.getPrecio(), p.isActivo()
            ))
            .collect(Collectors.toList());
    }

    /**
     * Crea una nueva venta en el POS.
     */
    @Transactional
    public com.matiasmeira.back_reservas.pos.dto.VentaResponseDTO crearVenta(VentaRequestDTO request) {
        Venta venta = registrarVenta(request);
        
        List<DetalleVentaRequestDTO> detalles = request.detalles();
        String numeroTransaccion = "TX-" + System.currentTimeMillis();

        return new com.matiasmeira.back_reservas.pos.dto.VentaResponseDTO(
            venta.getId(),
            venta.getEstablecimiento().getId(),
            venta.getUsuario().getId(),
            venta.getTotal(),
            venta.getMetodoPago(),
            venta.getFechaHora(),
            detalles,
            numeroTransaccion
        );
    }

    /**
     * Genera un reporte de caja para un día específico.
     */
    @Transactional(readOnly = true)
    public ReporteCajaDTO generarReporteCaja(Long establecimientoId, LocalDate fecha) {
        return obtenerReporteCajaDiario(establecimientoId, fecha);
    }

    /**
     * Genera un reporte de caja para un rango de fechas.
     */
    @Transactional(readOnly = true)
    public List<ReporteCajaDTO> generarReporteRango(Long establecimientoId, LocalDate fechaInicio, LocalDate fechaFin) {
        List<ReporteCajaDTO> reportes = new java.util.ArrayList<>();
        LocalDate current = fechaInicio;

        while (!current.isAfter(fechaFin)) {
            reportes.add(obtenerReporteCajaDiario(establecimientoId, current));
            current = current.plusDays(1);
        }

        return reportes;
    }

    /**
     * Obtiene el detalle de una venta por ID.
     */
    @Transactional(readOnly = true)
    public com.matiasmeira.back_reservas.pos.dto.VentaResponseDTO obtenerVentaPorId(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
            .orElseThrow(() -> new EntidadNoEncontradaException("Venta no encontrada"));

        List<DetalleVenta> detalles = detalleVentaRepository.findAll().stream()
            .filter(d -> d.getVenta().getId().equals(ventaId))
            .collect(Collectors.toList());

        List<DetalleVentaRequestDTO> detallesDTO = detalles.stream()
            .map(d -> new DetalleVentaRequestDTO(d.getProductoPos().getId(), d.getCantidad()))
            .collect(Collectors.toList());

        String numeroTransaccion = "TX-" + venta.getId();

        return new com.matiasmeira.back_reservas.pos.dto.VentaResponseDTO(
            venta.getId(),
            venta.getEstablecimiento().getId(),
            venta.getUsuario().getId(),
            venta.getTotal(),
            venta.getMetodoPago(),
            venta.getFechaHora(),
            detallesDTO,
            numeroTransaccion
        );
    }
}