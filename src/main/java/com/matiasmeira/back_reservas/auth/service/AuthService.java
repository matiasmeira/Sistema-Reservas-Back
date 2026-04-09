package com.matiasmeira.back_reservas.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matiasmeira.back_reservas.auth.dto.AuthResponseDTO;
import com.matiasmeira.back_reservas.auth.dto.LoginRequestDTO;
import com.matiasmeira.back_reservas.auth.dto.RegisterRequestDTO;
import com.matiasmeira.back_reservas.auth.model.Usuario;
import com.matiasmeira.back_reservas.auth.repository.UsuarioRepository;
import com.matiasmeira.back_reservas.auth.security.JwtService;
import com.matiasmeira.back_reservas.exception.AuthenticationFailedException;
import com.matiasmeira.back_reservas.exception.EntidadNoEncontradaException;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de autenticación que maneja login y registro de usuarios.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Autentica un usuario con sus credenciales y genera un token JWT.
     *
     * @param loginRequest Las credenciales del usuario
     * @return AuthResponseDTO con el token y datos del usuario
     * @throws AuthenticationFailedException Si las credenciales son inválidas
     */
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            Usuario usuario = usuarioRepository.findByEmail(loginRequest.email())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Usuario no encontrado"));

            String token = jwtService.generateToken(usuario);

            return new AuthResponseDTO(
                    token,
                    usuario.getEmail(),
                    usuario.getNombre(),
                    usuario.getRol(),
                    usuario.getId()
            );
        } catch (AuthenticationException ex) {
            throw new AuthenticationFailedException("Credenciales inválidas: " + ex.getMessage(), ex);
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param registerRequest Los datos del nuevo usuario
     * @return AuthResponseDTO con el token y datos del usuario creado
     * @throws IllegalArgumentException Si el email ya existe
     */
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO registerRequest) {
        if (usuarioRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .email(registerRequest.email())
                .nombre(registerRequest.nombre())
                .password(passwordEncoder.encode(registerRequest.password()))
                .rol(registerRequest.rol())
                .build();

        usuario = usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario);

        return new AuthResponseDTO(
                token,
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getRol(),
                usuario.getId()
        );
    }
}
