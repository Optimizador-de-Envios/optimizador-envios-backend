package com.sofka.optimizador_envios_backend.application.port.output;

import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;

import java.util.List;
import java.util.Optional;

public interface ConfirmacionPedidoRepository {
    Optional<ConfirmacionPedido> buscarPorTokenConfirmacion(String confirmationToken);

    List<ConfirmacionPedido> buscarPorUserId(String userId);

    ConfirmacionPedido guardar(ConfirmacionPedido confirmacionPedido);
}