package com.sofka.optimizador_envios_backend.application.port.input;

import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;

import java.util.List;

public interface ObtenerMisPedidosUseCase {
    List<ConfirmacionPedido> obtenerMisPedidos(String userId);
}
