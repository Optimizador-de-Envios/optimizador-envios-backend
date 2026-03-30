package com.sofka.optimizador_envios_backend.application.port.output;

import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;

public interface ConfirmacionPedidoRepository {
    ConfirmacionPedido guardar(ConfirmacionPedido confirmacionPedido);
}