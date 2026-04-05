package com.sofka.optimizador_envios_backend.domain.service;

import com.sofka.optimizador_envios_backend.domain.exception.PedidoInvalidoException;
import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.valueobject.ConfirmationToken;

import java.util.List;

public class ConfirmacionPedidoService {

    public ConfirmacionPedido confirmar(
            String userId,
            ConfirmationToken confirmationToken,
            Pedido pedido,
            Cotizacion opcionSeleccionada,
            List<Cotizacion> cotizacionesDisponibles,
            double distanciaKm
    ) {
        Cotizacion opcionValidada = validarOpcionSeleccionada(opcionSeleccionada, cotizacionesDisponibles);

        return ConfirmacionPedido.pendiente(userId, confirmationToken, pedido, distanciaKm, opcionValidada);
    }

    public ConfirmacionPedido confirmar(
            ConfirmationToken confirmationToken,
            Pedido pedido,
            Cotizacion opcionSeleccionada,
            List<Cotizacion> cotizacionesDisponibles,
            double distanciaKm
    ) {
        return confirmar(null, confirmationToken, pedido, opcionSeleccionada, cotizacionesDisponibles, distanciaKm);
    }

    private Cotizacion validarOpcionSeleccionada(
            Cotizacion opcionSeleccionada,
            List<Cotizacion> cotizacionesDisponibles
    ) {
        if (opcionSeleccionada == null) {
            throw new PedidoInvalidoException("Se debe seleccionar un proveedor para continuar");
        }

        return cotizacionesDisponibles.stream()
                .filter(cotizacion -> cotizacion.coincideCon(opcionSeleccionada))
                .findFirst()
                .orElseThrow(() -> new PedidoInvalidoException(
                        "La opcion seleccionada no coincide con las cotizaciones disponibles"
                ));
    }
}