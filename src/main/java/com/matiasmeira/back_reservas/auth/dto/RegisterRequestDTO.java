package com.matiasmeira.back_reservas.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.matiasmeira.back_reservas.auth.model.Rol;

/**
 * DTO para registrar un nuevo usuario.
 */
public record RegisterRequestDTO(
    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    String email,

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    String nombre,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password,

    Rol rol
) {}
