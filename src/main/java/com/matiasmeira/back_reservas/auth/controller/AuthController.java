package com.matiasmeira.back_reservas.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matiasmeira.back_reservas.auth.dto.AuthResponseDTO;
import com.matiasmeira.back_reservas.auth.dto.LoginRequestDTO;
import com.matiasmeira.back_reservas.auth.dto.RegisterRequestDTO;
import com.matiasmeira.back_reservas.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para autenticación de usuarios.
 * Proporciona endpoints públicos para login y registro.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint para autenticar un usuario con sus credenciales.
     *
     * @param loginRequest Email y contraseña del usuario
     * @return AuthResponseDTO con el token JWT generado
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     *
     * @param registerRequest Datos del nuevo usuario
     * @return AuthResponseDTO con el token JWT generado
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        AuthResponseDTO authResponse = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }
}
