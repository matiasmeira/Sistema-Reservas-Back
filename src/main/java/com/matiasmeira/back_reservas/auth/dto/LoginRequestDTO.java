package com.matiasmeira.back_reservas.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para las credenciales de login.
 */
public record LoginRequestDTO(
    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    String email,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password
) {}
