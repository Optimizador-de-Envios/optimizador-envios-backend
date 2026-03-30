package com.sofka.optimizador_envios_backend.application.usecase;

import com.sofka.optimizador_envios_backend.application.port.input.ObtenerRecomendacionUseCase;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Recomendacion;
import com.sofka.optimizador_envios_backend.domain.service.MotorRecomendacionService;
import org.springframework.stereotype.Service;

@Service
public class ObtenerRecomendacionUseCaseImpl implements ObtenerRecomendacionUseCase {

    private final CotizarPedidoService cotizarPedidoService;
    private final MotorRecomendacionService motorRecomendacionService;

    public ObtenerRecomendacionUseCaseImpl(
            CotizarPedidoService cotizarPedidoService,
            MotorRecomendacionService motorRecomendacionService
    ) {
        this.cotizarPedidoService = cotizarPedidoService;
        this.motorRecomendacionService = motorRecomendacionService;
    }

    @Override
    public Recomendacion obtenerRecomendacion(Pedido pedido) {
        ResultadoCotizacionPedido resultadoCotizacion = cotizarPedidoService.cotizar(pedido);

        return motorRecomendacionService.recomendar(resultadoCotizacion.cotizaciones(), pedido.prioridad());
    }
}
