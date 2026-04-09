# 🧪 EJEMPLOS DE USO - RestControllers

## Tabla de Contenidos
1. [Setup Inicial](#setup-inicial)
2. [EstablecimientoController](#establecimientocontroller)
3. [BookingController](#bookingcontroller)
4. [PosController](#poscontroller)
5. [PagosController](#pagoscontroller)
6. [Manejo de Errores](#manejo-de-errores)

---

## Setup Inicial

### 1. Iniciar la aplicación
```bash
cd c:\Users\USER\Desktop\back-reservas
.\mvnw.cmd spring-boot:run
```

### 2. Verificar que está corriendo
```bash
curl http://localhost:8080/back-reservas/actuator/health
# Response: {"status":"UP"}
```

### 3. Importar en Postman
```
Base URL: http://localhost:8080/back-reservas
```

---

## EstablecimientoController

### Ejemplo 1: Obtener todos los establecimientos

```bash
curl -X GET http://localhost:8080/back-reservas/api/establecimientos
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Club Universidad",
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
]
```

### Ejemplo 2: Buscar por cercanía geográfica

```bash
# Radio de 5km alrededor de coordenadas
curl -X GET "http://localhost:8080/back-reservas/api/establecimientos?latitud=-34.5&longitud=-58.4&radioKm=5"
```

### Ejemplo 3: Buscar por deporte

```bash
curl -X GET "http://localhost:8080/back-reservas/api/establecimientos?deporte=FUTBOL"
```

### Ejemplo 4: Búsqueda completa (cercanía + deporte)

```bash
curl -X GET "http://localhost:8080/back-reservas/api/establecimientos?latitud=-34.5&longitud=-58.4&radioKm=10&deporte=TENIS"
```

### Ejemplo 5: Obtener detalle de establecimiento

```bash
curl -X GET http://localhost:8080/back-reservas/api/establecimientos/1
```

**Response:**
```json
{
  "id": 1,
  "nombre": "Club Universidad",
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

### Ejemplo 6: Crear nuevo establecimiento

```bash
curl -X POST http://localhost:8080/back-reservas/api/establecimientos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Club MultiDeporte Zona Oeste",
    "direccion": "Calle 123, La Matanza",
    "latitud": -34.7,
    "longitud": -58.6,
    "horaApertura": "08:00",
    "horaCierre": "22:00",
    "tipoPlan": "STANDARD",
    "permitirReasignacion": false,
    "requiereSena": true,
    "porcentajeSena": 15.0,
    "estado": "ACTIVO",
    "usuarioId": 2,
    "amenityIds": [1, 3]
  }'
```

**Response (201 Created):**
```json
{
  "id": 2,
  "nombre": "Club MultiDeporte Zona Oeste",
  "direccion": "Calle 123, La Matanza",
  "latitud": -34.7,
  "longitud": -58.6,
  "horaApertura": "08:00",
  "horaCierre": "22:00",
  "tipoPlan": "STANDARD",
  "permitirReasignacion": false,
  "requiereSena": true,
  "porcentajeSena": 15.0,
  "estado": "ACTIVO",
  "usuarioId": 2,
  "amenityIds": [1, 3]
}
```

---

## BookingController

### Ejemplo 1: Consultar disponibilidad

```bash
curl -X GET "http://localhost:8080/back-reservas/api/booking/disponibilidad?fecha=2026-04-15&productoCanchaId=5"
```

**Response (200 OK):**
```json
[
  {
    "horaInicio": "09:00:00",
    "horaFin": "10:00:00",
    "disponible": true,
    "razonNoDisponible": null
  },
  {
    "horaInicio": "10:00:00",
    "horaFin": "11:00:00",
    "disponible": true,
    "razonNoDisponible": null
  },
  {
    "horaInicio": "11:00:00",
    "horaFin": "12:00:00",
    "disponible": false,
    "razonNoDisponible": "No disponible"
  },
  {
    "horaInicio": "18:00:00",
    "horaFin": "19:00:00",
    "disponible": true,
    "razonNoDisponible": null
  }
]
```

### Ejemplo 2: Crear reserva

```bash
curl -X POST http://localhost:8080/back-reservas/api/booking/reservar \
  -H "Content-Type: application/json" \
  -d '{
    "fechaReserva": "2026-04-15",
    "horaInicio": "18:00",
    "horaFin": "19:30",
    "productoCanchaId": 5,
    "usuarioId": 10,
    "permitirReasignacion": true
  }'
```

**Response (201 Created):**
```json
{
  "id": 125,
  "fechaReserva": "2026-04-15",
  "horaInicio": "18:00:00",
  "horaFin": "19:30:00",
  "precioTotal": 450.00,
  "estado": "PENDIENTE",
  "usuarioId": 10,
  "productoCanchaId": 5,
  "modulosAsignadosIds": [12, 13],
  "pagoId": 98
}
```

**Pasos siguientes:**
1. Usuario recibe `pagoId: 98`
2. Usuario accede a link de pago de Mercado Pago
3. Una vez pagado, el webhook notifica al backend
4. Sistema actualiza estado de pago y reserva

### Ejemplo 3: Obtener mis reservas

```bash
curl -X GET "http://localhost:8080/back-reservas/api/booking/mis-reservas?usuarioId=10"
```

**Response:**
```json
[
  {
    "id": 125,
    "fechaReserva": "2026-04-15",
    "horaInicio": "18:00:00",
    "horaFin": "19:30:00",
    "precioTotal": 450.00,
    "estado": "CONFIRMADA",
    "usuarioId": 10,
    "productoCanchaId": 5,
    "modulosAsignadosIds": [12, 13],
    "pagoId": 98
  },
  {
    "id": 124,
    "fechaReserva": "2026-04-10",
    "horaInicio": "20:00:00",
    "horaFin": "21:00:00",
    "precioTotal": 300.00,
    "estado": "CONFIRMADA",
    "usuarioId": 10,
    "productoCanchaId": 3,
    "modulosAsignadosIds": [8],
    "pagoId": 97
  }
]
```

### Ejemplo 4: Obtener reserva individual

```bash
curl -X GET http://localhost:8080/back-reservas/api/booking/125
```

### Ejemplo 5: Cancelar reserva

```bash
curl -X POST http://localhost:8080/back-reservas/api/booking/125/cancelar
```

**Response (204 No Content):** Sin body, solo headers

---

## PosController

### Ejemplo 1: Obtener catálogo de productos

```bash
curl -X GET http://localhost:8080/back-reservas/api/pos/productos/1
```

**Response:**
```json
[
  {
    "id": 10,
    "nombre": "Pizza Grande Mozzarella",
    "precio": 450.00,
    "activo": true
  },
  {
    "id": 11,
    "nombre": "Cerveza Artesanal Pack x6",
    "precio": 180.00,
    "activo": true
  },
  {
    "id": 12,
    "nombre": "Hamburguesa Premium",
    "precio": 320.00,
    "activo": true
  },
  {
    "id": 13,
    "nombre": "Gaseosa 2L",
    "precio": 150.00,
    "activo": true
  }
]
```

### Ejemplo 2: Registrar venta en POS

```bash
curl -X POST http://localhost:8080/back-reservas/api/pos/venta \
  -H "Content-Type: application/json" \
  -d '{
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
  }'
```

**Response (201 Created):**
```json
{
  "id": 50,
  "establecimientoId": 1,
  "usuarioId": 5,
  "montoTotal": 1080.00,
  "metodoPago": "EFECTIVO",
  "fechaHora": "2026-04-09T14:30:00",
  "detalles": [
    {
      "productoPosId": 10,
      "cantidad": 2
    },
    {
      "productoPosId": 11,
      "cantidad": 1
    }
  ],
  "numeroTransaccion": "TX-1712948200000"
}
```

### Ejemplo 3: Reporte diario de caja

```bash
curl -X GET "http://localhost:8080/back-reservas/api/pos/reporte-diario?establecimientoId=1"
```

**Response:**
```json
{
  "fecha": "2026-04-09",
  "totalVentas": 18500.00,
  "numeroVentas": 28
}
```

### Ejemplo 4: Reporte con fecha específica

```bash
curl -X GET "http://localhost:8080/back-reservas/api/pos/reporte-diario?establecimientoId=1&fecha=2026-04-08"
```

### Ejemplo 5: Reporte de rango de fechas

```bash
curl -X GET "http://localhost:8080/back-reservas/api/pos/reporte-rango?establecimientoId=1&fechaInicio=2026-04-01&fechaFin=2026-04-30"
```

**Response (Lista de reportes diarios):**
```json
[
  {
    "fecha": "2026-04-01",
    "totalVentas": 5200.00,
    "numeroVentas": 12
  },
  {
    "fecha": "2026-04-02",
    "totalVentas": 6800.00,
    "numeroVentas": 15
  },
  ...
  {
    "fecha": "2026-04-30",
    "totalVentas": 4500.00,
    "numeroVentas": 10
  }
]
```

### Ejemplo 6: Obtener detalle de venta individual

```bash
curl -X GET http://localhost:8080/back-reservas/api/pos/venta/50
```

---

## PagosController

### Ejemplo 1: Webhook de Mercado Pago (Automático)

**Nota:** Este endpoint es llamado AUTOMÁTICAMENTE por Mercado Pago, no por el cliente.

```bash
# Simulación local:
curl -X POST http://localhost:8080/back-reservas/api/pagos/webhook \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": "MP-98765432",
    "status": "approved",
    "reservaId": 125
  }'
```

**Response (200 OK):** Sin body, solo confirmación

**Interno del sistema:**
1. Sistema recibe webhook
2. Busca el pago por `paymentId`
3. Valida que `status = "approved"`
4. Actualiza pago a `APROBADO`
5. Actualiza reserva a `CONFIRMADA`
6. Notifica al usuario

### Ejemplo 2: Reintentar pago fallido

```bash
curl -X POST http://localhost:8080/back-reservas/api/pagos/75/reintentar
```

**Response (200 OK):**
```
Sin body, confirmación exitosa
```

**Casos de error:**
```bash
# Si el pago no está en status RECHAZADO
# → Status 400 Bad Request
```

---

## Manejo de Errores

### Ejemplo 1: Entidad no encontrada

```bash
curl -X GET http://localhost:8080/back-reservas/api/establecimientos/999
```

**Response (404 Not Found):**
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

### Ejemplo 2: Error de validación

```bash
curl -X POST http://localhost:8080/back-reservas/api/establecimientos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "",
    "latitud": "no-valido"
  }'
```

**Response (400 Bad Request):**
```json
{
  "timestamp": "2026-04-09T13:50:00",
  "status": 400,
  "error": "Error de Validación",
  "message": "Errores en la validación de los datos de entrada",
  "path": "/api/establecimientos",
  "validationErrors": {
    "nombre": "El nombre es obligatorio",
    "latitud": "La latitud debe estar entre -90 y 90"
  }
}
```

### Ejemplo 3: Reserva no disponible

```bash
curl -X POST http://localhost:8080/back-reservas/api/booking/reservar \
  -H "Content-Type: application/json" \
  -d '{
    "fechaReserva": "2026-04-15",
    "horaInicio": "18:00",
    "horaFin": "19:30",
    "productoCanchaId": 999,
    "usuarioId": 10,
    "permitirReasignacion": false
  }'
```

**Response (400 Bad Request):**
```json
{
  "timestamp": "2026-04-09T13:50:00",
  "status": 400,
  "error": "Reserva No Disponible",
  "message": "No hay combinación disponible para la reserva solicitada",
  "path": "/api/booking/reservar",
  "validationErrors": null
}
```

### Ejemplo 4: Error interno del servidor

```bash
# Cuando algo inesperado ocurre
curl -X GET http://localhost:8080/back-reservas/api/establecimientos
```

**Response (500 Internal Server Error):**
```json
{
  "timestamp": "2026-04-09T13:50:00",
  "status": 500,
  "error": "Error del Servidor",
  "message": "Ha ocurrido un error inesperado.",
  "path": "/api/establecimientos",
  "validationErrors": null
}
```

---

## Scripts de Prueba Completos

### Script 1: Flujo de Reserva Completa

```bash
#!/bin/bash

BASE_URL="http://localhost:8080/back-reservas"

# 1. Buscar establecimientos cercanos
echo "1. Buscando establecimientos..."
curl -X GET "$BASE_URL/api/establecimientos?deporte=FUTBOL"

# 2. Obtener disponibilidad
echo -e "\n2. Consultando disponibilidad..."
curl -X GET "$BASE_URL/api/booking/disponibilidad?fecha=2026-04-15&productoCanchaId=5"

# 3. Crear reserva
echo -e "\n3. Creando reserva..."
RESPUESTA=$(curl -X POST "$BASE_URL/api/booking/reservar" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaReserva": "2026-04-15",
    "horaInicio": "18:00",
    "horaFin": "19:30",
    "productoCanchaId": 5,
    "usuarioId": 10,
    "permitirReasignacion": true
  }')

echo "$RESPUESTA"

# Extraer ID de reserva (jq requiere estar instalado)
RESERVA_ID=$(echo $RESPUESTA | grep -oP '"id":\K[^,}]+' | head -1)
echo "Reserva ID: $RESERVA_ID"

# 4. Obtener mis reservas
echo -e "\n4. Obteniendo mis reservas..."
curl -X GET "$BASE_URL/api/booking/mis-reservas?usuarioId=10"

# 5. Simular webhook de pago
echo -e "\n5. Simulando pagopago aprobado..."
curl -X POST "$BASE_URL/api/pagos/webhook" \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": "MP-" $(date +%s),
    "status": "approved",
    "reservaId": '$RESERVA_ID'
  }'
```

### Script 2: Flujo de Venta POS

```bash
#!/bin/bash

BASE_URL="http://localhost:8080/back-reservas"

# 1. Obtener catálogo
echo "1. Obteniendo catálogo..."
curl -X GET "$BASE_URL/api/pos/productos/1"

# 2. Registrar venta
echo -e "\n2. Registrando venta..."
curl -X POST "$BASE_URL/api/pos/venta" \
  -H "Content-Type: application/json" \
  -d '{
    "establecimientoId": 1,
    "usuarioId": 5,
    "metodoPago": "EFECTIVO",
    "detalles": [
      {"productoPosId": 10, "cantidad": 2},
      {"productoPosId": 11, "cantidad": 1}
    ]
  }'

# 3. Obtener reporte diario
echo -e "\n3. Obteniendo reporte diario..."
curl -X GET "$BASE_URL/api/pos/reporte-diario?establecimientoId=1"

# 4. Obtener reporte de mes
echo -e "\n4. Obteniendo reporte de mes..."
curl -X GET "$BASE_URL/api/pos/reporte-rango?establecimientoId=1&fechaInicio=2026-04-01&fechaFin=2026-04-30"
```

---

## Notas Finales

- ✅ Todos los ejemplos asumen base URL: `http://localhost:8080/back-reservas`
- ✅ Los IDs usados son ejemplos, reemplazar con IDs reales
- ✅ El webhook de Mercado Pago es automático, no necesita solicitud manual
- ✅ Las fechas por como deben estar en formato ISO 8601: `YYYY-MM-DD`
- ✅ Las horas formato: `HH:MM` o `HH:MM:SS`

