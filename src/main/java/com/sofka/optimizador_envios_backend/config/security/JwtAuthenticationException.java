package com.sofka.optimizador_envios_backend.config.security;

public class JwtAuthenticationException extends RuntimeException {

    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}