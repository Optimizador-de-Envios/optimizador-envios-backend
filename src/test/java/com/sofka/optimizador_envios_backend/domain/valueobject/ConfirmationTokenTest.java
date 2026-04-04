package com.sofka.optimizador_envios_backend.domain.valueobject;

import com.sofka.optimizador_envios_backend.domain.exception.PedidoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfirmationTokenTest {

    @Test
    void dadoConfirmationTokenValido_cuandoSeCrea_entoncesDebeConservarSuValor() {
        ConfirmationToken token = ConfirmationToken.of("token-123");

        assertEquals("token-123", token.value());
    }

    @Test
    void dadoConfirmationTokenVacio_cuandoSeCrea_entoncesDebeLanzarExcepcion() {
        PedidoInvalidoException exception = assertThrows(
                PedidoInvalidoException.class,
                () -> ConfirmationToken.of("   ")
        );

        assertEquals("El confirmationToken es obligatorio", exception.getMessage());
    }
}