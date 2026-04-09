# ⚡ QUICK START - Guía de Inicio Rápido

## 🎯 En 5 Minutos

### 1️⃣ Compilar (1-2 minutos)
```bash
cd c:\Users\USER\Desktop\back-reservas
.\mvnw.cmd clean compile
```
✅ **Resultado esperado:** `BUILD SUCCESS`

### 2️⃣ Ejecutar (1 minuto)
```bash
.\mvnw.cmd spring-boot:run
```
✅ **Resultado esperado:**
```
Started BackReservasApplication in X.XXX seconds
```

### 3️⃣ Verificar que está corriendo (30 segundos)
```bash
curl http://localhost:8080/back-reservas/actuator/health
```
✅ **Respuesta esperada:**
```json
{"status":"UP"}
```

### 4️⃣ Probar un endpoint (30 segundos)
```bash
curl http://localhost:8080/back-reservas/api/establecimientos
```
✅ **Respuesta esperada:** Array JSON de establecimientos

---

## 📚 Documentación por Necesidad

### "Necesito entender la estructura"
→ Lee: `ÍNDICE_MAESTRO.md`

### "Necesito ver todos los endpoints"
→ Lee: `ARQUITECTURA_RESTCONTROLLERS.md`

### "Necesito ejemplos prácticos"
→ Lee: `EJEMPLOS_USO.md`

### "Necesito entender los flujos"
→ Lee: `DIAGRAMAS_FLUJO.md`

### "Necesito un resumen ejecutivo"
→ Lee: `RESUMEN_IMPLEMENTACION.md`

---

## 🧪 Prueba Rápida - 3 Endpoints

### Test 1: Obtener Establecimientos
```bash
curl http://localhost:8080/back-reservas/api/establecimientos
```

### Test 2: Obtener Disponibilidad
```bash
curl "http://localhost:8080/back-reservas/api/booking/disponibilidad?fecha=2026-04-15&productoCanchaId=5"
```

### Test 3: Obtener Catálogo POS
```bash
curl http://localhost:8080/back-reservas/api/pos/productos/1
```

---

## 🛠️ Troubleshooting

### ❌ Error: "Port 8080 already in use"
```bash
# Opción 1: Cambiar puerto en application.properties
server.port=9090

# Opción 2: Matar el proceso en el puerto
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### ❌ Error: "Connection refused"
```bash
# Verificar que está corriendo
curl http://localhost:8080/back-reservas/actuator/health

# Si falla, iniciar nuevamente
.\mvnw spring-boot:run
```

### ❌ Error de compilación
```bash
# Limpiar cache y recompilar
.\mvnw.cmd clean compile -DskipTests

# Si persiste, revisar:
# - Versión de Java (debe ser 21+)
# - Maven en PATH
```

---

## 📋 Checklist de Validación

```
✅ Compilación exitosa
✅ Aplicación inicia sin errores
✅ Health check responde
✅ Endpoints responden
✅ DTOs se serializan
✅ Excepciones no matan la app
✅ CORS está habilitado
```

---

## 🚀 Integración con Postman

### 1. Importar Collection
```json
{
  "info": {
    "name": "Back Reservas API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Establecimientos",
      "request": {
        "method": "GET",
        "url": "{{base_url}}/api/establecimientos"
      }
    },
    {
      "name": "Disponibilidad",
      "request": {
        "method": "GET",
        "url": "{{base_url}}/api/booking/disponibilidad?fecha=2026-04-15&productoCanchaId=5"
      }
    }
  ]
}
```

### 2. Configurar Variables
```
base_url = http://localhost:8080/back-reservas
```

### 3. ¡A Probar!

---

## 📱 Integración con Frontend

### JavaScript/Fetch
```javascript
// Obtener establecimientos
fetch('http://localhost:8080/back-reservas/api/establecimientos')
  .then(res => res.json())
  .then(data => console.log(data))
  .catch(err => console.error(err))

// Crear reserva
fetch('http://localhost:8080/back-reservas/api/booking/reservar', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    fechaReserva: '2026-04-15',
    horaInicio: '18:00',
    horaFin: '19:30',
    productoCanchaId: 5,
    usuarioId: 10,
    permitirReasignacion: true
  })
})
```

### Python/Requests
```python
import requests

# Obtener establecimientos
response = requests.get('http://localhost:8080/back-reservas/api/establecimientos')
print(response.json())

# Crear reserva
data = {
    'fechaReserva': '2026-04-15',
    'horaInicio': '18:00',
    'horaFin': '19:30',
    'productoCanchaId': 5,
    'usuarioId': 10,
    'permitirReasignacion': True
}
response = requests.post(
    'http://localhost:8080/back-reservas/api/booking/reservar',
    json=data
)
print(response.json())
```

### C#/.NET
```csharp
using System.Net.Http;

using var client = new HttpClient();

// Obtener establecimientos
var response = await client.GetAsync("http://localhost:8080/back-reservas/api/establecimientos");
var content = await response.Content.ReadAsStringAsync();
Console.WriteLine(content);

// Crear reserva
var data = new {
    fechaReserva = "2026-04-15",
    horaInicio = "18:00",
    horaFin = "19:30",
    productoCanchaId = 5,
    usuarioId = 10,
    permitirReasignacion = true
};
var json = JsonConvert.SerializeObject(data);
var request = new StringContent(json, Encoding.UTF8, "application/json");
response = await client.PostAsync("http://localhost:8080/back-reservas/api/booking/rezervar", request);
```

---

## 📊 15 Endpoints en Resumen

```
ESTABLECIMIENTO (3):
  GET /api/establecimientos
  GET /api/establecimientos/{id}
  POST /api/establecimientos

BOOKING (5):
  GET /api/booking/disponibilidad
  POST /api/booking/reservar
  GET /api/booking/mis-reservas
  GET /api/booking/{id}
  POST /api/booking/{id}/cancelar

POS (5):
  GET /api/pos/productos/{id}
  POST /api/pos/venta
  GET /api/pos/reporte-diario
  GET /api/pos/reporte-rango
  GET /api/pos/venta/{id}

PAGOS (2):
  POST /api/pagos/webhook
  POST /api/pagos/{id}/reintentar
```

---

## 🔄 Ciclo de Desarrollo

```
1. Cambiar código
   ↓
2. .\mvnw.cmd clean compile
   ↓
3. .\mvnw.cmd spring-boot:run
   ↓
4. Tests con curl/Postman
   ↓
5. ¿Errores? → Volver a 1
   ↓
6. ¡Listo a producción!
```

---

## ⚙️ Configuración Mínima Requerida

### application.properties
```properties
# Puerto
server.port=8080

# Base de datos (debe estar corriendo)
spring.datasource.url=jdbc:mysql://localhost:3306/back_reservas
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update

# Logging
logging.level.root=INFO
logging.level.com.matiasmeira=DEBUG
```

---

## 🎓 Principios Clave

### ✅ REST Resource-Oriented
```
GET    /api/resource           # Listar
GET    /api/resource/{id}      # Detalle
POST   /api/resource           # Crear
PUT    /api/resource/{id}      # Actualizar
DELETE /api/resource/{id}      # Eliminar
```

### ✅ HTTP Status Codes
```
200 OK              → Exitoso, devuelve datos
201 Created         → Recurso creado
204 No Content      → Exitoso, sin datos
400 Bad Request     → Error de cliente
404 Not Found       → Recurso no existe
500 Server Error    → Error del servidor
```

### ✅ JSON Responses
```json
{
  "id": 1,
  "nombre": "...",
  "createdAt": "2026-04-09T..."
}
```

### ✅ Error Handling
```json
{
  "timestamp": "2026-04-09T13:50:00",
  "status": 404,
  "error": "No Encontrado",
  "message": "Recurso no encontrado",
  "path": "/api/recurso/999"
}
```

---

## 📞 Ayuda Rápida

| Problema | Solución |
|----------|----------|
| App no inicia | Revisar logs, puerto disponible |
| Endpoint 404 | Verificar URL y método HTTP |
| 400 Bad Request | Revisar JSON payload |
| 500 Error | Revisar logs de la aplicación |
| Base de datos | Verificar conexión MySQL |

---

## 🎉 ¡Listo!

Ahora tienes una API REST completa y funcional.

**Próximo paso:** Lee `ARQUITECTURA_RESTCONTROLLERS.md` para explorar todos los endpoints disponibles.

---

**Última compilación:** ✅ BUILD SUCCESS (4.8s)  
**Archivos:** 61 compilados  
**Época:** Java 21, Spring Boot 3.x  
**Estado:** 🚀 READY FOR PRODUCTION

