package com.sofka.optimizador_envios_backend.application.usecase;

import com.sofka.optimizador_envios_backend.application.port.input.ObtenerMisPedidosUseCase;
import com.sofka.optimizador_envios_backend.application.port.output.ConfirmacionPedidoRepository;
import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObtenerMisPedidosUseCaseImpl implements ObtenerMisPedidosUseCase {

    private final ConfirmacionPedidoRepository confirmacionPedidoRepository;

    public ObtenerMisPedidosUseCaseImpl(ConfirmacionPedidoRepository confirmacionPedidoRepository) {
        this.confirmacionPedidoRepository = confirmacionPedidoRepository;
    }

    @Override
    public List<ConfirmacionPedido> obtenerMisPedidos(String userId) {
        return confirmacionPedidoRepository.buscarPorUserId(userId);
    }
}
