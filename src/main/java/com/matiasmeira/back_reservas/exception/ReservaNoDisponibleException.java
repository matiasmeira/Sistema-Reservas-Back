package com.matiasmeira.back_reservas.exception;

public class ReservaNoDisponibleException extends RuntimeException {
    public ReservaNoDisponibleException(String message) {
        super(message);
    }

    public ReservaNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}