package com.sofka.optimizador_envios_backend.application.usecase;

import com.sofka.optimizador_envios_backend.application.port.input.ConfirmarPedidoUseCase;
import com.sofka.optimizador_envios_backend.application.port.output.ConfirmacionPedidoRepository;
import com.sofka.optimizador_envios_backend.application.port.output.DistanciaClient;
import com.sofka.optimizador_envios_backend.application.port.output.ProveedorClient;
import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.service.ConfirmacionPedidoService;

import java.util.List;

public class ConfirmarPedidoUseCaseImpl implements ConfirmarPedidoUseCase {

    private final DistanciaClient distanciaClient;
    private final List<ProveedorClient> proveedores;
    private final ConfirmacionPedidoService confirmacionPedidoService;
    private final ConfirmacionPedidoRepository confirmacionPedidoRepository;

    public ConfirmarPedidoUseCaseImpl(
            DistanciaClient distanciaClient,
            List<ProveedorClient> proveedores,
            ConfirmacionPedidoService confirmacionPedidoService,
            ConfirmacionPedidoRepository confirmacionPedidoRepository
    ) {
        this.distanciaClient = distanciaClient;
        this.proveedores = proveedores;
        this.confirmacionPedidoService = confirmacionPedidoService;
        this.confirmacionPedidoRepository = confirmacionPedidoRepository;
    }

    @Override
    public ConfirmacionPedido confirmar(Pedido pedido, Cotizacion opcionSeleccionada) {
        double distanciaKm = distanciaClient.obtenerDistanciaKm(pedido.origen(), pedido.destino());

        List<Cotizacion> cotizaciones = proveedores.stream()
                .map(proveedor -> proveedor.cotizar(pedido, distanciaKm))
                .toList();

        ConfirmacionPedido confirmacionPendiente = confirmacionPedidoService.confirmar(
                pedido,
                opcionSeleccionada,
                cotizaciones,
                distanciaKm
        );

        return confirmacionPedidoRepository.guardar(confirmacionPendiente);
    }
}