package com.sofka.optimizador_envios_backend.application.port.input;

import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;

public interface ConfirmarPedidoUseCase {
    ConfirmacionPedido confirmar(String confirmationToken, Pedido pedido, Cotizacion opcionSeleccionada);

    ConfirmacionPedido confirmar(String userId, String confirmationToken, Pedido pedido, Cotizacion opcionSeleccionada);
}