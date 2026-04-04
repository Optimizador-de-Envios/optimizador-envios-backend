package com.sofka.optimizador_envios_backend.application.usecase;

import com.sofka.optimizador_envios_backend.application.port.input.ConfirmarPedidoUseCase;
import com.sofka.optimizador_envios_backend.application.port.output.ConfirmacionPedidoRepository;
import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.service.ConfirmacionPedidoService;
import com.sofka.optimizador_envios_backend.domain.valueobject.ConfirmationToken;
import org.springframework.stereotype.Service;

@Service
public class ConfirmarPedidoUseCaseImpl implements ConfirmarPedidoUseCase {

    private final CotizarPedidoService cotizarPedidoService;
    private final ConfirmacionPedidoService confirmacionPedidoService;
    private final ConfirmacionPedidoRepository confirmacionPedidoRepository;

    public ConfirmarPedidoUseCaseImpl(
            CotizarPedidoService cotizarPedidoService,
            ConfirmacionPedidoService confirmacionPedidoService,
            ConfirmacionPedidoRepository confirmacionPedidoRepository
    ) {
        this.cotizarPedidoService = cotizarPedidoService;
        this.confirmacionPedidoService = confirmacionPedidoService;
        this.confirmacionPedidoRepository = confirmacionPedidoRepository;
    }

    @Override
    public ConfirmacionPedido confirmar(String confirmationToken, Pedido pedido, Cotizacion opcionSeleccionada) {
        ConfirmationToken token = ConfirmationToken.of(confirmationToken);

        return confirmacionPedidoRepository.buscarPorTokenConfirmacion(token.value())
            .orElseGet(() -> confirmarNuevoIntento(token, pedido, opcionSeleccionada));
    }

    private ConfirmacionPedido confirmarNuevoIntento(
            ConfirmationToken confirmationToken,
            Pedido pedido,
            Cotizacion opcionSeleccionada
    ) {
        ResultadoCotizacionPedido resultadoCotizacion = cotizarPedidoService.cotizar(pedido);

        ConfirmacionPedido confirmacionPendiente = confirmacionPedidoService.confirmar(
                confirmationToken,
                pedido,
                opcionSeleccionada,
                resultadoCotizacion.cotizaciones(),
                resultadoCotizacion.distanciaKm()
        );

        return confirmacionPedidoRepository.guardar(confirmacionPendiente);
    }
}