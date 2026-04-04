package com.sofka.optimizador_envios_backend.domain.valueobject;

import com.sofka.optimizador_envios_backend.domain.exception.PedidoInvalidoException;

public record ConfirmationToken(String value) {

    public ConfirmationToken {
        if (value == null || value.isBlank()) {
            throw new PedidoInvalidoException("El confirmationToken es obligatorio");
        }
    }

    public static ConfirmationToken of(String value) {
        return new ConfirmationToken(value);
    }
}