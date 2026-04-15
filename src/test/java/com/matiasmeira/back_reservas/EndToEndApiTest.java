package com.matiasmeira.back_reservas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EndToEndApiTest {

    @Autowired
    private MockMvc mockMvc;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        // Registrar usuario para obtener token (simulado)
        // En un test real, podrías usar un servicio mock o base de datos en memoria
        authToken = "mock-jwt-token";
    }

    // ==================== AUTENTICACIÓN ====================

    @Test
    public void testRegisterUser() throws Exception {
        String registerJson = """
            {
                "email": "test@example.com",
                "nombre": "Test User",
                "password": "password123",
                "rol": "CLIENTE"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void testLoginUser() throws Exception {
        String loginJson = """
            {
                "email": "test@example.com",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    // ==================== BOOKING - RESERVAS ====================

    @Test
    @WithMockUser(roles = "CLIENTE")
    public void testGetDisponibilidad() throws Exception {
        mockMvc.perform(get("/api/booking/disponibilidad")
                .param("fecha", "2026-04-20")
                .param("productoCanchaId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    public void testCreateReserva() throws Exception {
        String reservaJson = """
            {
                "fechaReserva": "2026-04-20",
                "horaInicio": "10:00",
                "horaFin": "11:00",
                "productoCanchaId": 1,
                "usuarioId": 1,
                "permitirReasignacion": false
            }
            """;

        mockMvc.perform(post("/api/booking/reservar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservaJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    public void testGetMisReservas() throws Exception {
        mockMvc.perform(get("/api/booking/mis-reservas")
                .param("usuarioId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    public void testGetReservaById() throws Exception {
        mockMvc.perform(get("/api/booking/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    public void testCancelarReserva() throws Exception {
        mockMvc.perform(post("/api/booking/1/cancelar"))
                .andExpect(status().isNoContent());
    }

    // ==================== BOOKING - MÓDULOS FÍSICOS ====================

    @Test
    @WithMockUser(roles = "DUENIO")
    public void testGetModulosFisicos() throws Exception {
        mockMvc.perform(get("/api/booking/modulos-fisicos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DUENIO")
    public void testCreateModuloFisico() throws Exception {
        String moduloJson = """
            {
                "nombre": "Cancha Test",
                "estado": "DISPONIBLE",
                "establecimientoId": 1
            }
            """;

        mockMvc.perform(post("/api/booking/modulos-fisicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(moduloJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    public void testCreateModuloFisicoForbidden() throws Exception {
        String moduloJson = """
            {
                "nombre": "Cancha Test",
                "estado": "DISPONIBLE",
                "establecimientoId": 1
            }
            """;

        mockMvc.perform(post("/api/booking/modulos-fisicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(moduloJson))
                .andExpect(status().isForbidden());
    }

    // ==================== BOOKING - PRODUCTOS CANCHA ====================

    @Test
    @WithMockUser(roles = "DUENIO")
    public void testGetProductosCancha() throws Exception {
        mockMvc.perform(get("/api/booking/productos-cancha"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DUENIO")
    public void testCreateProductoCancha() throws Exception {
        String productoJson = """
            {
                "nombre": "Fútbol 5 Test",
                "deporte": "FUTBOL",
                "superficie": "Césped sintético",
                "modulosNecesarios": 1,
                "duracionMinima": 60,
                "intervaloPaso": 60,
                "establecimientoId": 1
            }
            """;

        mockMvc.perform(post("/api/booking/productos-cancha")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productoJson))
                .andExpect(status().isCreated());
    }

    // ==================== BOOKING - COMBINACIONES POSIBLES ====================

    @Test
    @WithMockUser(roles = "DUENIO")
    public void testGetCombinacionesPosibles() throws Exception {
        mockMvc.perform(get("/api/booking/combinaciones-posibles"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DUENIO")
    public void testCreateCombinacionPosible() throws Exception {
        String combinacionJson = """
            {
                "nombre": "Combinación Test",
                "productoCanchaId": 1,
                "modulosFisicosIds": [1, 2]
            }
            """;

        mockMvc.perform(post("/api/booking/combinaciones-posibles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(combinacionJson))
                .andExpect(status().isCreated());
    }

    // ==================== BOOKING - HORARIOS PRECIO ====================

    @Test
    @WithMockUser(roles = "DUENIO")
    public void testGetHorariosPrecio() throws Exception {
        mockMvc.perform(get("/api/booking/horarios-precio"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DUENIO")
    public void testCreateHorarioPrecio() throws Exception {
        String horarioJson = """
            {
                "diaSemana": 1,
                "horaInicio": "09:00",
                "horaFin": "18:00",
                "precioHora": 50.00,
                "productoCanchaId": 1
            }
            """;

        mockMvc.perform(post("/api/booking/horarios-precio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(horarioJson))
                .andExpect(status().isCreated());
    }

    // ==================== ESTABLECIMIENTOS ====================

    @Test
    @WithMockUser(roles = "CLIENTE")
    public void testGetEstablecimientos() throws Exception {
        mockMvc.perform(get("/api/establecimientos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DUENIO")
    public void testCreateEstablecimiento() throws Exception {
        String establecimientoJson = """
            {
                "nombre": "Club Test",
                "descripcion": "Club para pruebas",
                "direccion": "Calle Test 123",
                "telefono": "+5491123456789",
                "email": "club@test.com",
                "latitud": -34.6037,
                "longitud": -58.3816,
                "duenioId": 1
            }
            """;

        mockMvc.perform(post("/api/establecimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(establecimientoJson))
                .andExpect(status().isCreated());
    }

    // ==================== POS ====================

    @Test
    @WithMockUser(roles = "DUENIO")
    public void testGetCatalogoProductos() throws Exception {
        mockMvc.perform(get("/api/pos/productos/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DUENIO")
    public void testCreateVenta() throws Exception {
        String ventaJson = """
            {
                "establecimientoId": 1,
                "productos": [
                    {
                        "productoId": 1,
                        "cantidad": 2,
                        "precioUnitario": 5.00
                    }
                ],
                "metodoPago": "EFECTIVO",
                "montoTotal": 10.00
            }
            """;

        mockMvc.perform(post("/api/pos/venta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ventaJson))
                .andExpect(status().isCreated());
    }

    // ==================== PAGOS ====================

    @Test
    public void testWebhookMercadoPago() throws Exception {
        String webhookJson = """
            {
                "id": "123456789",
                "status": "approved",
                "external_reference": "RESERVA_1"
            }
            """;

        mockMvc.perform(post("/api/pagos/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(webhookJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DUENIO")
    public void testReintentarPago() throws Exception {
        mockMvc.perform(post("/api/pagos/1/reintentar"))
                .andExpect(status().isOk());
    }

    // ==================== VALIDACIÓN ====================

    @Test
    public void testRegisterInvalidEmail() throws Exception {
        String invalidRegisterJson = """
            {
                "email": "invalid-email",
                "nombre": "Test",
                "password": "123",
                "rol": "CLIENTE"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRegisterJson))
                .andExpect(status().isBadRequest());
    }
}