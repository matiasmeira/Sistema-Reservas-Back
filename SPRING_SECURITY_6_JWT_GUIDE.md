# 🔒 Guía Completa: Spring Security 6 + JWT en back-reservas

## 📋 Tabla de Contenidos

1. [Introducción](#introducción)
2. [Arquitectura de Seguridad](#arquitectura-de-seguridad)
3. [Configuración Inicial](#configuración-inicial)
4. [Componentes de Seguridad](#componentes-de-seguridad)
5. [Flujo de Autenticación](#flujo-de-autenticación)
6. [Autorización y Control de Acceso](#autorización-y-control-de-acceso)
7. [Ejemplos de Uso](#ejemplos-de-uso)
8. [Configuración de Producción](#configuración-de-producción)
9. [Solución de Problemas](#solución-de-problemas)

---

## Introducción

Este documento describe la implementación de **Spring Security 6** con **JWT (JSON Web Tokens)** en la aplicación back-reservas. Este sistema proporciona:

- ✅ **Autenticación Stateless**: No se almacenan sesiones en el servidor
- ✅ **Autorización basada en Roles**: ADMIN, DUENIO, CLIENTE
- ✅ **Tokens JWT**: Tokens seguros y auto-contenidos
- ✅ **Encriptación BCrypt**: Contraseñas seguras
- ✅ **Manejo Centralizado de Errores**: GlobalExceptionHandler integrado

---

## Arquitectura de Seguridad

```
┌─────────────────────────────────────────────────────────────────┐
│                     Cliente (Browser/App)                       │
└──────────────────────────┬──────────────────────────────────────┘
                           │ HTTP Request
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│              Spring Security FilterChain                        │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ JwtAuthenticationFilter                                  │  │
│  │ - Extrae token del header "Authorization: Bearer <token>"│  │
│  │ - Valida firma y expiración                              │  │
│  │ - Establece SecurityContext                              │  │
│  └──────────────────────────────────────────────────────────┘  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│              SecurityFilterChain Authorization                  │
│  - Verifica roles requeridos para cada endpoint                │
│  - Endpoints públicos (sin token): /api/auth/**, etc.          │
│  - Endpoints protegidos: Requieren rol específico              │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Controller/Service Layer                      │
│              Lógica de negocio de la aplicación                │
└─────────────────────────────────────────────────────────────────┘
```

---

## Configuración Inicial

### 1. Dependencias Maven

```xml
<!-- JWT (JJWT 0.12.3) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>

<!-- Spring Security -->
<!-- (Incluido en Spring Boot 3.5.13) -->
```

### 2. Configuración de Propiedades

**`application.properties`:**

```properties
# JWT Configuration
security.jwt.secret-key=mySecretKeyForJWTTokenGenerationAndValidationPurposesOnlyChangeThisInProductionEnvironment12345
security.jwt.expiration=86400000  # 24 horas en milisegundos

# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Database Configuration
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

---

## Componentes de Seguridad

### 1. JwtService

**Responsabilidad**: Gestionar la generación, validación y extracción de claims de tokens JWT.

**Ubicación**: `src/main/java/com/matiasmeira/back_reservas/auth/security/JwtService.java`

**Métodos Principales**:

```java
// Genera un token para un usuario
public String generateToken(Usuario usuario)

// Extrae el email (subject) del token
public String extractUsername(String token)

// Valida que el token sea válido para un usuario
public boolean isTokenValid(String token, UserDetails userDetails)

// Extrae el rol del token
public String extractRole(String token)

// Extrae un claim específico
public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
```

**Algoritmo**: HS256 (HMAC-SHA256) con clave secreta configurable.

---

### 2. UserDetailsServiceImpl

**Responsabilidad**: Cargar datos del usuario desde la base de datos para Spring Security.

**Ubicación**: `src/main/java/com/matiasmeira/back_reservas/auth/security/UserDetailsServiceImpl.java`

**Métodos Principales**:

```java
// Carga un usuario por email
UserDetails loadUserByUsername(String email)

// Convierte Rol enum a GrantedAuthority
Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario)
```

**Relación**: Se conecta con `UsuarioRepository` para obtener usuarios de PostgreSQL.

---

### 3. JwtAuthenticationFilter

**Responsabilidad**: Filtro HTTP que valida el token JWT en cada solicitud.

**Ubicación**: `src/main/java/com/matiasmeira/back_reservas/auth/security/JwtAuthenticationFilter.java`

**Flujo**:

1. Intercepta cada request HTTP
2. Extrae el token del header `Authorization: Bearer <token>`
3. Valida el token usando `JwtService`
4. Carga el usuario usando `UserDetailsServiceImpl`
5. Establece el `SecurityContext`
6. Continúa con el siguiente filtro

**Estrategia de Error**: Si el token no es válido, continúa sin autenticación (permite endpoints públicos).

---

### 4. AuthService

**Responsabilidad**: Lógica de negocio para autenticación (login y registro).

**Ubicación**: `src/main/java/com/matiasmeira/back_reservas/auth/service/AuthService.java`

**Métodos**:

```java
// Autentica un usuario con email y contraseña
AuthResponseDTO login(LoginRequestDTO request)

// Registra un nuevo usuario
AuthResponseDTO register(RegisterRequestDTO request)
```

**Validaciones**:

- ✅ Email válido (formato correcto)
- ✅ Contraseña >= 6 caracteres
- ✅ Nombre entre 3-100 caracteres
- ✅ Email único en la base de datos

---

### 5. SecurityConfig

**Responsabilidad**: Configuración centralizada de Spring Security y la cadena de filtros.

**Ubicación**: `src/main/java/com/matiasmeira/back_reservas/auth/security/SecurityConfig.java`

**Configuraciones Clave**:

```java
// 1. Password Encoder (BCrypt)
@Bean
public PasswordEncoder passwordEncoder()

// 2. Authentication Provider
@Bean
public AuthenticationProvider authenticationProvider()

// 3. Authentication Manager
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config)

// 4. Security Filter Chain
@Bean
public SecurityFilterChain filterChain(HttpSecurity http)
```

**Seguridad del Filter Chain**:

| Característica | Configuración |
|---|---|
| CSRF | Deshabilitado (API Stateless) |
| Session | STATELESS (sin cookies de sesión) |
| CORS | Permisivo (configurable) |
| Filtro JWT | Inyectado antes de UsernamePasswordAuthenticationFilter |

---

### 6. AuthController

**Responsabilidad**: Exponer endpoints públicos para autenticación.

**Ubicación**: `src/main/java/com/matiasmeira/back_reservas/auth/controller/AuthController.java`

**Endpoints**:

```
POST /api/auth/login
- Request: LoginRequestDTO { email, password }
- Response: AuthResponseDTO { token, email, nombre, rol, usuarioId }
- Status: 200 OK

POST /api/auth/register
- Request: RegisterRequestDTO { email, nombre, password, rol }
- Response: AuthResponseDTO { token, email, nombre, rol, usuarioId }
- Status: 201 Created
```

---

### 7. Excepciones de Seguridad

#### AuthenticationFailedException

```java
// Se lanza cuando:
// - Email o contraseña incorrectos
// - Usuario no encontrado

// Manejo en GlobalExceptionHandler:
// HTTP Status: 401 Unauthorized
// Error Response: { error: "Autenticación Fallida", message: "..." }
```

#### JwtTokenException

```java
// Se lanza cuando:
// - Firma del token inválida
// - Token expirado
// - Formato de token incorrecto
// - Claim vacío

// Manejo en GlobalExceptionHandler:
// HTTP Status: 401 Unauthorized
// Error Response: { error: "Token Inválido", message: "..." }
```

---

## Flujo de Autenticación

### 1. Registro (Register)

```
Cliente                          Servidor
   │                                │
   │ POST /api/auth/register       │
   │ { email, nombre, password }   │
   ├───────────────────────────────>│
   │                                │
   │                         Valida datos
   │                      Email único?
   │                    Encripta contraseña
   │                       Crea Usuario
   │                    Genera JWT Token
   │                                │
   │      AuthResponseDTO           │
   │<───────────────────────────────┤
   │                                │
```

**Request**:
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "cliente@example.com",
  "nombre": "Juan Pérez",
  "password": "miPassword123",
  "rol": "CLIENTE"
}
```

**Response** (201 Created):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "cliente@example.com",
  "nombre": "Juan Pérez",
  "rol": "CLIENTE",
  "usuarioId": 1
}
```

---

### 2. Login

```
Cliente                          Servidor
   │                                │
   │ POST /api/auth/login           │
   │ { email, password }            │
   ├───────────────────────────────>│
   │                                │
   │                    Busca Usuario
   │                  por email
   │               Valida contraseña
   │              (BCrypt comparison)
   │                Genera JWT Token
   │                                │
   │      AuthResponseDTO           │
   │<───────────────────────────────┤
   │                                │
```

**Request**:
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "cliente@example.com",
  "password": "miPassword123"
}
```

**Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "cliente@example.com",
  "nombre": "Juan Pérez",
  "rol": "CLIENTE",
  "usuarioId": 1
}
```

---

### 3. Solicitud Protegida

```
Cliente                          Servidor
   │                                │
   │ POST /api/booking/reservar     │
   │ Authorization: Bearer <token>  │
   │ { datos de reserva }           │
   ├───────────────────────────────>│
   │                                │
   │                   JwtAuthenticationFilter
   │                      Extrae token
   │                   Valida firma (HS256)
   │                   Valida expiración
   │                      Extrae rol
   │                   Carga usuario
   │                   Establece SecurityContext
   │                                │
   │                   SecurityFilterChain
   │                    ¿Rol = CLIENTE?
   │                        Sí ✓
   │                                │
   │                   Ejecuta servicio
   │                   Procesa reserva
   │                                │
   │      Response (200 OK)         │
   │<───────────────────────────────┤
   │                                │
```

**Request**:
```bash
POST http://localhost:8080/api/booking/reservar
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "moduloFisicoId": 1,
  "fechaReserva": "2026-04-15",
  "horaInicio": "14:00:00",
  "horaFin": "15:00:00"
}
```

---

## Autorización y Control de Acceso

### Estructura de Roles

```
┌─────────────────────────────────────────────────────────┐
│                    Rol.java (Enum)                     │
├─────────────────────────────────────────────────────────┤
│ - ADMIN      → Acceso total, gestión de sistema        │
│ - DUENIO     → Crear/actualizar establecimientos,      │
│               gestionar módulos, ver POS               │
│ - CLIENTE    → Crear reservas, ver disponibilidad      │
└─────────────────────────────────────────────────────────┘
```

### Reglas de Autorización

| Endpoint | Método | Público | Rol Requerido |
|----------|--------|---------|---------------|
| /api/auth/login | POST | ✅ | - |
| /api/auth/register | POST | ✅ | - |
| /api/establecimientos | GET | ✅ | - |
| /api/establecimientos/{id} | GET | ✅ | - |
| /api/booking/disponibilidad | GET | ✅ | - |
| /api/pagos/webhook | POST | ✅ | - |
| /api/establecimientos | POST | ❌ | DUENIO |
| /api/establecimientos/{id} | PUT | ❌ | DUENIO |
| /api/establecimientos/{id} | DELETE | ❌ | DUENIO |
| /api/pos/** | GET/POST | ❌ | DUENIO, ADMIN |
| /api/booking/reservar | POST | ❌ | CLIENTE |
| /api/booking/mis-reservas | GET | ❌ | CLIENTE |
| /api/pagos/{id}/reintentar | POST | ❌ | ADMIN |

### Implementación en Código

```java
// En SecurityConfig.filterChain():

// Endpoints públicos
.requestMatchers("/api/auth/**").permitAll()
.requestMatchers("/api/pagos/webhook").permitAll()
.requestMatchers("/api/establecimientos").permitAll()

// Endpoints protegidos
.requestMatchers(HttpMethod.POST, "/api/establecimientos")
    .hasRole("DUENIO")

.requestMatchers("/api/booking/reservar")
    .hasRole("CLIENTE")

.requestMatchers("/api/pos/**")
    .hasAnyRole("DUENIO", "ADMIN")
```

---

## Ejemplos de Uso

### 1. Cliente cURL

#### Registro

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan@example.com",
    "nombre": "Juan García",
    "password": "MiPassword123",
    "rol": "CLIENTE"
  }'
```

#### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan@example.com",
    "password": "MiPassword123"
  }'

# Response incluye token
```

#### Solicitud Protegida

```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X POST http://localhost:8080/api/booking/reservar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "moduloFisicoId": 1,
    "fechaReserva": "2026-04-15",
    "horaInicio": "14:00:00",
    "horaFin": "15:00:00"
  }'
```

### 2. Cliente JavaScript/Fetch

```javascript
// Registro
const registerResponse = await fetch('/api/auth/register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'cliente@example.com',
    nombre: 'María López',
    password: 'Password123',
    rol: 'CLIENTE'
  })
});

const authData = await registerResponse.json();
const token = authData.token; // Guardar token

// Usar token en solicitudes posteriores
const reservaResponse = await fetch('/api/booking/reservar', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    moduloFisicoId: 1,
    fechaReserva: '2026-04-15',
    horaInicio: '14:00:00',
    horaFin: '15:00:00'
  })
});
```

### 3. Cliente Postman

1. **Crear colección**: "back-reservas"

2. **Variables de colección**:
   ```
   base_url: http://localhost:8080/api
   token: (dejar vacío inicialmente)
   ```

3. **Request Login**:
   ```
   POST {{base_url}}/auth/login
   Body (raw JSON):
   {
     "email": "admin@example.com",
     "password": "admin123"
   }
   ```

4. **Script de Post-response** (en Login request):
   ```javascript
   if (pm.response.code === 200) {
     var jsonData = pm.response.json();
     pm.collectionVariables.set("token", jsonData.token);
   }
   ```

5. **Request Protegida**:
   ```
   POST {{base_url}}/booking/reservar
   Headers:
   - Authorization: Bearer {{token}}
   
   Body (raw JSON):
   { ... }
   ```

---

## Configuración de Producción

### ⚠️ CRÍTICO: Cambiar Secret Key

**Por favor cambia la clave secreta en `application-prod.properties`:**

```properties
# ❌ NO USAR ESTA CLAVE EN PRODUCCIÓN
security.jwt.secret-key=mySecretKeyForJWTTokenGenerationAndValidationPurposesOnlyChangeThisInProductionEnvironment12345

# ✅ USA UNA CLAVE GENERADA
# Genera una clave con:
# echo -n "tu-clave-aleatoria-segura" | base64

security.jwt.secret-key=<generar-una-clave-aleatoria-segura-de-256-bits-o-mas>
```

### Script para Generar Clave Segura

**Windows (PowerShell)**:
```powershell
$bytes = [System.Text.Encoding]::UTF8.GetBytes((New-Guid).ToString() + (Get-Random))
$base64 = [Convert]::ToBase64String($bytes)
Write-Host "Clave: $base64"
```

**Linux/Mac (Bash)**:
```bash
openssl rand -base64 32
```

### Configuración HTTPS

```properties
# application-prod.properties
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=<contraseña>
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

### Variables de Entorno

```properties
# application.properties
security.jwt.secret-key=${JWT_SECRET_KEY}
security.jwt.expiration=${JWT_EXPIRATION:86400000}
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
```

---

## Solución de Problemas

### ❌ Error: "Token inválido o expirado"

**Causas posibles**:

1. Token ha expirado (> 24 horas por defecto)
   - **Solución**: Hacer login de nuevo para obtener nuevo token

2. Token modificado o corrupto
   - **Solución**: Verificar que el token se copia completo sin espacios

3. Secret key diferente entre signing y validation
   - **Solución**: Verificar que `security.jwt.secret-key` es ignual

**Debug**:
```bash
# Decodificar token (en jwt.io)
# 1. Ir a https://jwt.io
# 2. Pegar el token
# 3. Verificar claims y expiración
```

---

### ❌ Error: "Acceso Denegado (403)"

**Causas**:

1. Rol insuficiente para el endpoint
   - **Solución**: Verificar rol en token vs rol requerido

2. Usuario no tiene el rol asignado en BD
   - **Solución**: Verificar tabla `usuario` columna `rol`

3. Token no incluido en header
   - **Solución**: Verificar formato: `Authorization: Bearer <token>`

**Verificación**:
```bash
# En JwtService, agrega logging:
log.info("Rol extraído del token: {}", extractRole(token));
log.info("Roles requeridos para endpoint: {}", requiredRoles);
```

---

### ❌ Error: "Usuario no encontrado"

**Causas**:

1. Email no existe en BD
   - **Solución**: Verificar email en login

2. Usuario fue eliminado después de generar token
   - **Solución**: Generar nuevo token

---

### ❌ Error de Compilación: "cannot find symbol: method parserBuilder()"

**Solución**:

Updatear `JwtService.extractAllClaims()`:

```java
// INCORRECTO (JJWT versiones antiguas)
return Jwts.parserBuilder()
    .setSigningKey(...)
    .build()
    .parseClaimsJws(token)
    .getBody();

// CORRECTO (JJWT 0.12.3+)
return Jwts.parser()
    .verifyWith(...)
    .build()
    .parseSignedClaims(token)
    .getPayload();
```

---

### ⚠️ Performance: Validación de Token

**Operaciones por request**:

1. Extrae token del header: O(1)
2. Descodifica JWT: O(1)
3. Valida firma HMAC: O(1)
4. Valida expiración: O(1)
5. Carga usuario desde BD: O(1) con índice en email

**Optimización recomendada**:

```java
// Agregar caché de usuarios
@Cacheable(value = "usuarios", key = "#email")
public Usuario findByEmail(String email) {
    return usuarioRepository.findByEmail(email);
}
```

---

### 📱 Testing en Dispositivos Móviles

**Postman Mobile App**:

1. Crear solicitud `POST /auth/login`
2. Guardar token en variable de colección
3. Usar variable `{{token}}` en header Authorization

**React Native / Flutter**:

```javascript
// React Native
const token = await AsyncStorage.getItem('authToken');

fetch('http://server/api/booking/reservar', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({...})
});
```

---

## Checkklist de Implementación

- ✅ Dependencias JWT en pom.xml (JJWT 0.12.3)
- ✅ JwtService implementado
- ✅ UserDetailsServiceImpl implementado
- ✅ JwtAuthenticationFilter configurado
- ✅ SecurityConfig con FilterChain
- ✅ AuthService (login/register)
- ✅ AuthController con endpoints
- ✅ Excepciones de seguridad creadas
- ✅ GlobalExceptionHandler mejorado
- ✅ DTOs validados (LoginRequestDTO, RegisterRequestDTO, AuthResponseDTO)
- ✅ application.properties con JWT config
- ✅ Compilación exitosa ✓ BUILD SUCCESS (5.4s, 72 archivos)

---

## Recursos Adicionales

- **JJWT Documentación**: https://github.com/jwtk/jjwt
- **Spring Security Docs**: https://spring.io/projects/spring-security
- **JWT.io**: https://jwt.io (herramienta para decodificar tokens)
- **OWASP JWT Cheat Sheet**: https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet.html

---

**Última actualización**: 2026-04-09
**Versión**: 1.0
**Autor**: GitHub Copilot
