package com.sofka.optimizador_envios_backend.domain.service;

import com.sofka.optimizador_envios_backend.domain.exception.PedidoInvalidoException;
import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;

import java.util.List;

public class ConfirmacionPedidoService {

    public ConfirmacionPedido confirmar(
            Pedido pedido,
            Cotizacion opcionSeleccionada,
            List<Cotizacion> cotizacionesDisponibles,
            double distanciaKm
    ) {
        if (opcionSeleccionada == null) {
            throw new PedidoInvalidoException("Se debe seleccionar un proveedor para continuar");
        }

        boolean coincideConCotizaciones = cotizacionesDisponibles.stream()
                .anyMatch(cotizacion -> cotizacion.nombreProveedor().equals(opcionSeleccionada.nombreProveedor())
                        && cotizacion.costo() == opcionSeleccionada.costo()
                        && cotizacion.diasEntrega() == opcionSeleccionada.diasEntrega());

        if (!coincideConCotizaciones) {
            throw new PedidoInvalidoException("La opcion seleccionada no coincide con las cotizaciones disponibles");
        }

        return new ConfirmacionPedido(null, pedido, distanciaKm, opcionSeleccionada);
    }
}