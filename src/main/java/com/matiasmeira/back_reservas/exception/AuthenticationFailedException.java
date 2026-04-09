package com.matiasmeira.back_reservas.exception;

/**
 * Excepción lanzada cuando falla la autenticación.
 */
public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }

    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
