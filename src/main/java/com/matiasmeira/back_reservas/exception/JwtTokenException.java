package com.matiasmeira.back_reservas.exception;

/**
 * Excepción lanzada cuando el token JWT es inválido o expiró.
 */
public class JwtTokenException extends RuntimeException {
    public JwtTokenException(String message) {
        super(message);
    }

    public JwtTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
