package com.matiasmeira.back_reservas.auth.dto;

import com.matiasmeira.back_reservas.auth.model.Rol;

/**
 * DTO para la respuesta de autenticación.
 * Contiene el token JWT y datos básicos del usuario.
 */
public record AuthResponseDTO(
    String token,
    String email,
    String nombre,
    Rol rol,
    Long usuarioId
) {}
