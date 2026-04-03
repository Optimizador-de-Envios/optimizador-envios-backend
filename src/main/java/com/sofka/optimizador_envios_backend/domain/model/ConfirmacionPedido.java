package com.sofka.optimizador_envios_backend.domain.model;

import com.sofka.optimizador_envios_backend.domain.exception.PedidoInvalidoException;

public record ConfirmacionPedido(
                String id,
                String confirmationToken,
                Pedido pedido,
                double distanciaKm,
                Cotizacion opcionSeleccionada
) {

        public ConfirmacionPedido {
                if (confirmationToken == null || confirmationToken.isBlank()) {
                        throw new PedidoInvalidoException("El confirmationToken es obligatorio");
                }
        }

        public static ConfirmacionPedido pendiente(
                        String confirmationToken,
                        Pedido pedido,
                        double distanciaKm,
                        Cotizacion opcionSeleccionada
        ) {
                return new ConfirmacionPedido(null, confirmationToken, pedido, distanciaKm, opcionSeleccionada);
        }
}