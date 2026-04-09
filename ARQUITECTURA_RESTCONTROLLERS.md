# RestControllers - Sistema de Reservas de Canchas Modulares y POS

## Resumen General

Se han creado **5 RestControllers** completos con:
- ✅ Anotaciones `@RestController` y `@RequestMapping`
- ✅ Inyección de dependencias con `@RequiredArgsConstructor`
- ✅ Manejo de estados HTTP con `ResponseEntity`
- ✅ Validación con `@Valid`
- ✅ DTOs para transferencia de datos
- ✅ CORS habilitado con `@CrossOrigin`
- ✅ Manejo global de excepciones con `@RestControllerAdvice`

---

## 1. EstablecimientoController
**Ubicación:** `com.matiasmeira.back_reservas.establecimiento.controller`

### Endpoints

| Método | Endpoint | Descripción | Respuesta |
|--------|----------|-------------|-----------|
| GET | `/api/establecimientos` | Buscar establecimientos con filtros | 200 OK |
| GET | `/api/establecimientos/{id}` | Obtener detalle de un establecimiento | 200 OK |
| POST | `/api/establecimientos` | Crear nuevo establecimiento (solo Dueños) | 201 Created |

### Parámetros de búsqueda
- `latitud` (Double, opcional): Latitud para búsqueda cercana
- `longitud` (Double, opcional): Longitud para búsqueda cercana
- `radioKm` (Double, opcional): Radio de búsqueda en km (default: 10km)
- `deporte` (Enum, opcional): Tipo de deporte a filtrar

### Ejemplos de Uso

```bash
# Obtener todos los establecimientos
GET /api/establecimientos

# Buscar por cercanía y deporte
GET /api/establecimientos?latitud=-34.5&longitud=-58.4&radioKm=15&deporte=FUTBOL

# Obtener detalle de un establecimiento
GET /api/establecimientos/1

# Crear nuevo establecimiento
POST /api/establecimientos
Content-Type: application/json
{
  "nombre": "Club MultiDeporte",
  "direccion": "Av. Libertador 1000",
  "latitud": -34.5,
  "longitud": -58.4,
  "horaApertura": "09:00",
  "horaCierre": "23:00",
  "tipoPlan": "PREMIUM",
  "permitirReasignacion": true,
  "requiereSena": true,
  "porcentajeSena": 20.0,
  "estado": "ACTIVO",
  "usuarioId": 1,
  "amenityIds": [1, 2, 3]
}
```

---

## 2. BookingController
**Ubicación:** `com.matiasmeira.back_reservas.booking.controller`

### Endpoints

| Método | Endpoint | Descripción | Respuesta |
|--------|----------|-------------|-----------|
| GET | `/api/booking/disponibilidad` | Obtener slots disponibles | 200 OK |
| POST | `/api/booking/reservar` | Crear nueva reserva | 201 Created |
| GET | `/api/booking/mis-reservas` | Historial del cliente | 200 OK |
| GET | `/api/booking/{reservaId}` | Detalle de una reserva | 200 OK |
| POST | `/api/booking/{reservaId}/cancelar` | Cancelar reserva | 204 No Content |

### Ejemplos de Uso

```bash
# Obtener disponibilidad de slots
GET /api/booking/disponibilidad?fecha=2026-04-15&productoCanchaId=5

# Crear nueva reserva
POST /api/booking/reservar
Content-Type: application/json
{
  "fechaReserva": "2026-04-15",
  "horaInicio": "18:00",
  "horaFin": "19:30",
  "productoCanchaId": 5,
  "usuarioId": 10,
  "permitirReasignacion": true
}

# Obtener mis reservas
GET /api/booking/mis-reservas?usuarioId=10

# Obtener detalle de una reserva
GET /api/booking/25

# Cancelar una reserva
POST /api/booking/25/cancelar
```

### Respuesta de Disponibilidad

```json
[
  {
    "horaInicio": "18:00",
    "horaFin": "19:00",
    "disponible": true,
    "razonNoDisponible": null
  },
  {
    "horaInicio": "19:00",
    "horaFin": "20:00",
    "disponible": false,
    "razonNoDisponible": "No disponible"
  }
]
```

### Respuesta de Reserva Creada

```json
{
  "id": 125,
  "fechaReserva": "2026-04-15",
  "horaInicio": "18:00",
  "horaFin": "19:30",
  "precioTotal": 450.00,
  "estado": "PENDIENTE",
  "usuarioId": 10,
  "productoCanchaId": 5,
  "modulosAsignadosIds": [12, 13],
  "pagoId": 98
}
```

---

## 3. PosController
**Ubicación:** `com.matiasmeira.back_reservas.pos.controller`

### Endpoints

| Método | Endpoint | Descripción | Respuesta |
|--------|----------|-------------|-----------|
| GET | `/api/pos/productos/{establecimientoId}` | Catálogo de productos | 200 OK |
| POST | `/api/pos/venta` | Registrar venta en POS | 201 Created |
| GET | `/api/pos/reporte-diario` | Reporte de caja del día | 200 OK |
| GET | `/api/pos/reporte-rango` | Reporte de rango de fechas | 200 OK |
| GET | `/api/pos/venta/{ventaId}` | Detalle de una venta | 200 OK |

### Ejemplos de Uso

```bash
# Obtener catálogo de productos
GET /api/pos/productos/1

# Registrar una venta
POST /api/pos/venta
Content-Type: application/json
{
  "establecimientoId": 1,
  "usuarioId": 5,
  "metodoPago": "EFECTIVO",
  "detalles": [
    {
      "productoPosId": 10,
      "cantidad": 2
    },
    {
      "productoPosId": 11,
      "cantidad": 1
    }
  ]
}

# Obtener reporte diario
GET /api/pos/reporte-diario?establecimientoId=1

# Obtener reporte diario específico
GET /api/pos/reporte-diario?establecimientoId=1&fecha=2026-04-09

# Obtener reporte de rango
GET /api/pos/reporte-rango?establecimientoId=1&fechaInicio=2026-04-01&fechaFin=2026-04-30

# Obtener detalle de venta
GET /api/pos/venta/50
```

### Respuesta de Catálogo

```json
[
  {
    "id": 10,
    "nombre": "Pizza Grande",
    "precio": 250.00,
    "activo": true
  },
  {
    "id": 11,
    "nombre": "Cerveza Pack x6",
    "precio": 180.00,
    "activo": true
  }
]
```

### Respuesta de Reporte Diario

```json
{
  "fecha": "2026-04-09",
  "totalVentas": 5350.00,
  "numeroVentas": 12
}
```

---

## 4. PagosController
**Ubicación:** `com.matiasmeira.back_reservas.pagos.controller`

### Endpoints

| Método | Endpoint | Descripción | Respuesta | Autenticación |
|--------|----------|-------------|-----------|---------------|
| POST | `/api/pagos/webhook` | Webhook de Mercado Pago (IPN) | 200 OK | ❌ Público |
| POST | `/api/pagos/{pagoId}/reintentar` | Reintentar pago fallido | 200 OK | ✅ Requerida |

### Flujo de Pagos

```
1. Usuario crea reserva → Pago creado en estado PENDIENTE
2. Usuario paga en Mercado Pago
3. Mercado Pago notifica → POST /api/pagos/webhook
4. Sistema actualiza estado del pago
5. Si APROBADO → Reserva pasa a CONFIRMADA
```

### Ejemplos de Uso

```bash
# Webhook IPN de Mercado Pago (enviado por Mercado Pago, NO por cliente)
POST /api/pagos/webhook
Content-Type: application/json
{
  "paymentId": "MP-98765432",
  "status": "approved",
  "reservaId": 125
}

# Reintentar un pago rechazado
POST /api/pagos/75/reintentar
```

### Configuración en Mercado Pago

1. Ir a Configuración → Integraciones
2. Configurar URL de webhook: `https://tu-dominio.com/api/pagos/webhook`
3. Seleccionar eventos: `payment.success`, `payment.failure`

---

## 5. Manejo Global de Excepciones
**Ubicación:** `com.matiasmeira.back_reservas.exception.GlobalExceptionHandler`

### Excepciones Manejadas

| Excepción | Status HTTP | Ejemplo |
|-----------|------------|---------|
| `EntidadNoEncontradaException` | 404 Not Found | Establecimiento no encontrado |
| `ReservaNoDisponibleException` | 400 Bad Request | No hay módulos disponibles |
| `MethodArgumentNotValidException` | 400 Bad Request | Errores de validación |
| `IllegalStateException` | 400 Bad Request | Estado inválido |
| `RuntimeException` | 500 Internal Server Error | Error inesperado |

### Respuesta de Error Estándar

```json
{
  "timestamp": "2026-04-09T13:50:00",
  "status": 404,
  "error": "No Encontrado",
  "message": "Establecimiento no encontrado",
  "path": "/api/establecimientos/999",
  "validationErrors": null
}
```

### Respuesta de Error de Validación

```json
{
  "timestamp": "2026-04-09T13:50:00",
  "status": 400,
  "error": "Error de Validación",
  "message": "Errores en la validación de los datos de entrada",
  "path": "/api/establecimientos",
  "validationErrors": {
    "nombre": "El nombre es obligatorio",
    "latitud": "La latitud debe ser válida"
  }
}
```

---

## DTOs Utilizados

### Request DTOs
- `EstablecimientoRequestDTO` - Crear/actualizar establecimiento
- `ReservaRequestDTO` - Crear reserva
- `VentaRequestDTO` - Registrar venta
- `DetalleVentaRequestDTO` - Detalle de venta
- `MpPaymentDTO` - Notificación de Mercado Pago

### Response DTOs
- `EstablecimientoResponseDTO` - Datos de establecimiento
- `ReservaResponseDTO` - Datos de reserva
- `VentaResponseDTO` - Datos de venta
- `ReporteCajaDTO` - Reporte de caja
- `ProductoDTO` - Producto POS
- `DisponibilidadDTO` - Slot disponible

---

## Características de Seguridad

### CORS
- Habilitado para todos los orígenes (`*`)
- Cache de 1 hora (3600 segundos)

### Validación
- `@Valid` en todos los endpoints POST/PUT
- Actualización automática de constraints

### Transacciones
- `@Transactional` en servicios que modifican datos
- Rollback automático en excepciones

---

## Configuración Recomendada en `application.properties`

```properties
# Servidor
server.port=8080
server.servlet.context-path=/back-reservas

# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/back_reservas
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update

# Logging
logging.level.com.matiasmeira=DEBUG
logging.level.org.springframework=INFO

# Mercado Pago (agregar tus credenciales)
mercado-pago.access-token=YOUR_ACCESS_TOKEN
mercado-pago.webhook-url=https://tu-dominio.com/api/pagos/webhook
```

---

## Pruebas con cURL

```bash
# Test EstablecimientoController
curl -X GET http://localhost:8080/back-reservas/api/establecimientos

# Test BookingController
curl -X GET "http://localhost:8080/back-reservas/api/booking/disponibilidad?fecha=2026-04-15&productoCanchaId=5"

# Test PosController
curl -X GET http://localhost:8080/back-reservas/api/pos/productos/1

# Test con JSON
curl -X POST http://localhost:8080/back-reservas/api/establecimientos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Club Test",
    "direccion": "Calle Test",
    "latitud": -34.5,
    "longitud": -58.4,
    "horaApertura": "09:00",
    "horaCierre": "23:00",
    "tipoPlan": "FREE",
    "permitirReasignacion": false,
    "requiereSena": false,
    "porcentajeSena": 0,
    "estado": "ACTIVO",
    "usuarioId": 1,
    "amenityIds": []
  }'
```

---

## Notas Importantes

1. **Webhook Mercado Pago**: El endpoint `/api/pagos/webhook` debe estar **público** (sin autenticación)
2. **Validación**: Todos los DTOs request utilizan `@Valid` automáticamente
3. **Transacciones**: Los servicios manejan transacciones automáticamente con `@Transactional`
4. **Errores**: Todos los errores retornan un JSON estandarizado con timestamp, status, mensaje
5. **CORS**: Habilitado globalmente pero puede limitarse según necesidad

---

## TODO - Próximos Pasos

- [ ] Agregar autenticación JWT
- [ ] Implementar paginación en endpoints de listado
- [ ] Agregar rate limiting
- [ ] Implementar caché con Redis
- [ ] Agregar tests unitarios e integración
- [ ] Documentación Swagger/OpenAPI
- [ ] Agregar logs desde controladores

