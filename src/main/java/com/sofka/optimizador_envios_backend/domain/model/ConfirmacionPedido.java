package com.sofka.optimizador_envios_backend.domain.model;

import com.sofka.optimizador_envios_backend.domain.exception.PedidoInvalidoException;
import com.sofka.optimizador_envios_backend.domain.valueobject.ConfirmationToken;

public record ConfirmacionPedido(
        String id,
        ConfirmationToken confirmationToken,
        Pedido pedido,
        double distanciaKm,
        Cotizacion opcionSeleccionada
) {

        public ConfirmacionPedido {
                if (confirmationToken == null) {
                        throw new PedidoInvalidoException("El confirmationToken es obligatorio");
                }
        }

        public static ConfirmacionPedido pendiente(
                ConfirmationToken confirmationToken,
                Pedido pedido,
                double distanciaKm,
                Cotizacion opcionSeleccionada
        ) {
                return new ConfirmacionPedido(null, confirmationToken, pedido, distanciaKm, opcionSeleccionada);
        }
}