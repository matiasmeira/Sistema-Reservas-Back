# Diagramas de Flujo - Sistema de Reservas

## 1. FLUJO DE RESERVA COMPLETO

```
┌─────────────────┐
│   Cliente Web   │
└────────┬────────┘
         │
         │ GET /api/establecimientos
         │
    ┌────▼────────────────┐
    │ EstablecimientoCtrl  │────► EstablecimientoService ────► DB
    ├─────────────────────┤
    │ - buscar            │
    │ - detalle           │
    │ - crear             │
    └────┬────────────────┘
         │
         │ GET /api/booking/disponibilidad
         │
    ┌────▼──────────────────┐
    │   BookingController   │
    ├──────────────────────┤
    │ - slots disponibles   │──► ReservaService
    │ - crear reserva       │    ├─ valida reglas
    │ - mis reservas        │    ├─ calcula precio
    │ - cancelar            │    └─ crea asignaciones
    └────┬──────────────────┘
         │
         │ POST /api/booking/reservar
         │ {fechaReserva, horaInicio, horaFin, productoCanchaId}
         │
    ┌────▼──────────────────────────┐
    │ Reserva CREADA (PENDIENTE)     │
    │ - Estado: PENDIENTE            │
    │ - Pago: PENDIENTE              │
    │ - Módulos: ASIGNADOS           │
    └────┬──────────────────────────┘
         │
         │ User -> Mercado Pago UI
         │
    ┌────▼────────────────┐
    │  Mercado Pago       │
    │  (Usuario paga)     │
    └────┬────────────────┘
         │
         │ Webhook IPN
         │ POST /api/pagos/webhook
         │
    ┌────▼────────────────────────────┐
    │   PagosController               │
    │ procesarWebhookMercadoPago()     │──► PagoService
    └────┬────────────────────────────┘
         │
    ┌────▼──────────────────────────┐
    │ SI status = "approved"         │
    │ - Pago: APROBADO              │
    │ - Reserva: CONFIRMADA         │
    │ - Notificar Cliente           │
    └───────────────────────────────┘
```

## 2. FLUJO DE VENTA POS

```
┌─────────────────┐
│  Empleado POS   │
└────────┬────────┘
         │
         │ GET /api/pos/productos/{establecimientoId}
         │
    ┌────▼─────────────────┐
    │  PosController        │
    │  obtenerCatalogo()    │──► POSService ---► DB
    └────┬─────────────────┘
         │
         ├─ [Pizza]         450.00
         ├─ [Cerveza]       180.00
         ├─ [Hamburguesa]   320.00
         └─ [Gaseosa]       150.00
         │
         │ POST /api/pos/venta
         │ {establecimientoId, usuarioId, detalles[], metodoPago}
         │
    ┌────▼─────────────────────────┐
    │ Venta REGISTRADA              │
    ├───────────────────────────────┤
    │ Fecha: 2026-04-09 14:30       │
    │ Total: 1080.00                │
    │ Transacción: TX-1712948200000 │
    └───────────────────────────────┘
         │
         │ GET /api/pos/reporte-diario
         │
    ┌────▼──────────────────┐
    │ Reporte de Caja       │
    ├──────────────────────┤
    │ Fecha: 2026-04-09    │
    │ Total: 18500.00      │
    │ Ventas: 28           │
    └──────────────────────┘
```

## 3. FLUJO DE DISPONIBILIDAD DE MÓDULOS

```
ProductoCancha
    │
    ├─ Deporte: FUTBOL
    ├─ Duracion Minima: 60 mins
    ├─ Intervalo Paso: 30 mins
    │
    └─ CombinacionesPosibles[]
        │
        ├─ Combinacion 1: [Modulo A, Modulo B]
        ├─ Combinacion 2: [Modulo C, Modulo D]
        └─ Combinacion 3: [Modulo A, Modulo C]
            │
            └─ ModuloFisico
                ├─ id: A
                ├─ nombre: "Cancha Principal"
                ├─ estado: OPERATIVO
                │
                └─ AsignacionesReservas[]
                    ├─ Reserva 1: 18:00-19:00 (PENDIENTE)
                    ├─ Reserva 2: 19:30-21:00 (CONFIRMADA)
                    └─ [DISPONIBLE] 21:00 en adelante


REQUEST: ¿Disponible el 15-04 18:00-19:00?
│
├─ VALIDAR reglas de tiempo
├─ BUSCAR CombinacionPosible con módulos libres
├─ SI NO encontrada → INTENTAR REASIGNACIÓN
└─ RESPONSE: DisponibilidadDTO[]
```

## 4. ARQUITECTURA DE CAPAS

```
┌─────────────────────────────────────────────────────────┐
│            REST CONTROLLERS (HTTP)                      │
├──────────────────────────────────────────────────────────┤
│ EstablecimientoController │ BookingController          │
│ PosController             │ PagosController            │
├──────────────────────────────────────────────────────────┤
│            @RequestMapping + @RestController             │
└────┬──────────────────────────────────────────────────┬──┘
     │                                                  │
     │ @Valid validation                               │
     │                                                  │
┌────▼──────────────────────────────────────────────────▼──┐
│              SERVICES (@Service Layer)                   │
├──────────────────────────────────────────────────────────┤
│ EstablecimientoService                                   │
│ - buscarPorCercaniaYDeporte()                           │
│ - obtenerPorId()                                         │
│ - crear()                                                │
├──────────────────────────────────────────────────────────┤
│ ReservaService                                           │
│ - crearReserva()                                         │
│ - obtenerSlotsDisponibles()                             │
│ - obtenerReservasPorUsuario()                           │
│ - cancelarReserva()                                      │
├──────────────────────────────────────────────────────────┤
│ POSService                                               │
│ - crearVenta()                                           │
│ - generarReporteCaja()                                   │
│ - obtenerProductosPorEstablecimiento()                   │
├──────────────────────────────────────────────────────────┤
│ PagoService                                              │
│ - procesarWebhook()                                      │
│ - reintentarPago()                                       │
└────┬──────────────────────────────────────────────────┬──┘
     │                                                  │
     │ @Transactional                                   │
     │                                                  │
┌────▼──────────────────────────────────────────────────▼──┐
│            REPOSITORIES (JPA)                            │
├──────────────────────────────────────────────────────────┤
│ EstablecimientoRepository                                │
│ ReservaRepository                                        │
│ ProductoCanchaRepository                                 │
│ VentaRepository                                          │
│ PagoRepository                                           │
│ ... etc                                                  │
└────┬──────────────────────────────────────────────────┬──┘
     │                                                  │
     │ SQL Queries                                      │
     │                                                  │
┌────▼──────────────────────────────────────────────────▼──┐
│               DATABASE (MySQL/PostgreSQL)               │
└──────────────────────────────────────────────────────────┘
```

## 5. MANEJO DE ERRORES GLOBAL

```
                    HTTP REQUEST
                         │
                         ▼
                    Controller
                         │
                    try/catch
                         │
        ┌────────────────┼────────────────┐
        │                │                │
        ▼                ▼                ▼
   EXITOSO        VALIDACION        NEGOCIO
        │          FALLIDA           FALLIDA
        │              │                │
        │              ▼                ▼
        │   MethodArgument       EntidadNo
        │   NotValid             Encontrada
        │   Exception             Exception
        │                             │
        │                ┌───────────┘
        │                │
        │      ReservaNo
        │      Disponible
        │      Exception
        │                │
        ├────────────────┤
        │                │
        ▼                ▼
    200 OK         GlobalExceptionHandler
                   (@RestControllerAdvice)
                           │
                ┌──────────┼──────────┐
                │          │          │
                ▼          ▼          ▼
            400        404        500
          Bad Req    Not Found   Error
            │          │          │
            └──────────┼──────────┘
                       │
                       ▼
            ErrorResponse JSON
            {
              timestamp,
              status,
              error,
              message,
              path,
              validationErrors
            }
```

## 6. FLUJO DE AUTENTICACIÓN (Futuro)

```
┌─────────────────────────────────────────┐
│        Implementación Recomendada        │
├─────────────────────────────────────────┤
│                                          │
│  1. Agregar JwtTokenProvider            │
│  2. SecurityFilterChain bean            │
│  3. @PreAuthorize en Controllers        │
│  4. AuthenticationController            │
│                                          │
│  POST /api/auth/login                   │
│  {username, password}                   │
│              │                           │
│              ▼                           │
│  JWT Token Response                     │
│  {token, refreshToken}                  │
│              │                           │
│              ▼                           │
│  Authorization: Bearer <token>          │
│              │                           │
│              ▼                           │
│  JwtAuthenticationFilter                │
│              │                           │
│              ▼                           │
│  Request validado ✅                    │
│                                          │
└─────────────────────────────────────────┘
```

## 7. INTEGRACIÓN CON MERCADO PAGO

```
┌──────────────────────────────────────────────────────────┐
│              FLUJO DE PAGO MP                            │
└──────────────────────────────────────────────────────────┘

Backend                    Cliente              Mercado Pago

  │                          │                      │
  │ 1. crearReserva()        │                      │
  ├──────────────────────────┤                      │
  │ 2. crearPago()           │                      │
  │ (MpPaymentId = null)     │                      │
  │                          │                      │
  │ 3. Request a MP          │                      │
  ├─────────────────────────────────────────────────┤
  │                          │ 4. Link de pago     │
  │ 5. Response (paymentId)  │◄─────────────────────┤
  ├──────────────────────────┤                      │
  │                          │ 6. Accede a MP      │
  │                          ├────────────────────►│
  │                          │ 7. Ingresa datos    │
  │                          │ de pago             │
  │                          │                     │
  │                          │ 8. Paga ✓          │
  │                          │                     │
  │ 9. Webhook IPN           │◄───────────────────┤
  │ POST /api/pagos/webhook  │                     │
  ├──────────────────────────┤                     │
  │ 10. procesarWebhook()    │                     │
  │ - Valida estado          │                     │
  │ - Actualiza Pago         │                     │
  │ - Confirma Reserva       │                     │
  │ - Notifica Cliente       │                     │

SEGURIDAD:
  ✅ Webhook es público (sin JWT)
  ✅ Validar firma de MP (implementar)
  ✅ Idempotencia (evitar duplicados)
  ✅ Logs de todas las transacciones
```

## 8. ESTRUCTURA DE DATOS - DIAGRAMA ER SIMPLIFICADO

```
┌─────────────────────┐
│   Usuario           │
├─────────────────────┤
│ id                  │
│ email (PK)          │
│ password            │
│ nombre              │
│ rol                 │
└────────┬────────────┘
         │ 1:N
         │
  ┌──────┴────────────────────┐
  │                           │
  ▼                           ▼
┌─────────────────┐    ┌──────────────────┐
│ Establecimiento │◄───│ Amenity          │
├─────────────────┤    ├──────────────────┤
│ id              │    │ id               │
│ nombre          │    │ nombre           │
│ latitud         │    │ descripcion      │
│ longitud        │    └──────────────────┘
│ horaApertura    │
│ horaCierre      │
└────────┬────────┘
         │ 1:N
         │
         ▼
┌──────────────────┐
│ ProductoCancha   │───► Deporte (Enum)
├──────────────────┤
│ id               │
│ nombre           │
│ deporte          │
└────────┬─────────┘
         │ 1:N
         │
         ▼
┌───────────────────────┐
│ CombinacionPosible    │
├───────────────────────┤
│ id                    │
│ modulosCount          │───► ModuloFisico (M:N)
└────────────┬──────────┘
             │ 1:N
             │
             ▼
┌────────────────┐
│ Reserva        │◄─────────────────┐
├────────────────┤                  │
│ id             │                  │
│ fecha          │                  │
│ horaInicio     │                  │
│ horaFin        │       1:1        │
│ estado         │                  │
│ precioTotal    │                  │
└────────┬───────┘                  │
         │ 1:N                      │
         │                          │
         ▼                          │
┌────────────────────┐       ┌──────┴────────┐
│ AsignacionReserva  │       │ Pago          │
├────────────────────┤       ├───────────────┤
│ id                 │       │ id            │
│ moduloFisico       │       │ montoTotal    │
│ reserva            │       │ estadoPago    │
└────────────────────┘       │ mpPaymentId   │
                             └───────────────┘
```

---

## Conclusión

La arquitectura implementa:
- ✅ **REST API completa** con 4 controladores funcionales
- ✅ **Manejo de excepciones global** centralizado
- ✅ **Validación de datos** en entrada
- ✅ **Transacciones ACID** en operaciones críticas
- ✅ **DTOs** para aislamiento de capas
- ✅ **CORS** habilitado para consumo desde web/mobile
- ✅ **Integración con Mercado Pago** via webhooks
- ✅ **Lógica de módulos modulares** para canchas
- ✅ **Reportes de caja** para POS

