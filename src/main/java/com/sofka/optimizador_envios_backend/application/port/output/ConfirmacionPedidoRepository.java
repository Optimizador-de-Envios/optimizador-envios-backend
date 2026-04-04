package com.sofka.optimizador_envios_backend.application.port.output;

import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;

import java.util.Optional;

public interface ConfirmacionPedidoRepository {
    Optional<ConfirmacionPedido> buscarPorTokenConfirmacion(String confirmationToken);

    ConfirmacionPedido guardar(ConfirmacionPedido confirmacionPedido);
}