package com.sofka.optimizador_envios_backend.domain.model;

import com.sofka.optimizador_envios_backend.domain.exception.PedidoInvalidoException;
import com.sofka.optimizador_envios_backend.domain.valueobject.ConfirmationToken;

import java.time.Instant;

public record ConfirmacionPedido(
        String id,
        String userId,
        ConfirmationToken confirmationToken,
        Pedido pedido,
        double distanciaKm,
        Cotizacion opcionSeleccionada,
        Instant createdAt
) {

        public ConfirmacionPedido {
                if (confirmationToken == null) {
                        throw new PedidoInvalidoException("El confirmationToken es obligatorio");
                }

                if (userId != null && userId.isBlank()) {
                        throw new PedidoInvalidoException("El userId es obligatorio");
                }
        }

        public ConfirmacionPedido(
                String id,
                ConfirmationToken confirmationToken,
                Pedido pedido,
                double distanciaKm,
                Cotizacion opcionSeleccionada
        ) {
                this(id, null, confirmationToken, pedido, distanciaKm, opcionSeleccionada, null);
        }

        public ConfirmacionPedido(
                String id,
                String userId,
                ConfirmationToken confirmationToken,
                Pedido pedido,
                double distanciaKm,
                Cotizacion opcionSeleccionada
        ) {
                this(id, userId, confirmationToken, pedido, distanciaKm, opcionSeleccionada, null);
        }

        public static ConfirmacionPedido pendiente(
                String userId,
                ConfirmationToken confirmationToken,
                Pedido pedido,
                double distanciaKm,
                Cotizacion opcionSeleccionada
        ) {
                if (userId == null || userId.isBlank()) {
                        throw new PedidoInvalidoException("El userId es obligatorio");
                }

                return new ConfirmacionPedido(null, userId, confirmationToken, pedido, distanciaKm, opcionSeleccionada, null);
        }

        public static ConfirmacionPedido pendiente(
                ConfirmationToken confirmationToken,
                Pedido pedido,
                double distanciaKm,
                Cotizacion opcionSeleccionada
        ) {
                return new ConfirmacionPedido(null, null, confirmationToken, pedido, distanciaKm, opcionSeleccionada, null);
        }
}