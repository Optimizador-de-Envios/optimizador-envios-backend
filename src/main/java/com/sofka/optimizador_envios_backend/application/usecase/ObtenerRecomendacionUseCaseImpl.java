package com.sofka.optimizador_envios_backend.application.usecase;

import com.sofka.optimizador_envios_backend.application.port.input.ObtenerRecomendacionUseCase;
import com.sofka.optimizador_envios_backend.application.port.output.DistanciaClient;
import com.sofka.optimizador_envios_backend.application.port.output.ProveedorClient;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Recomendacion;
import com.sofka.optimizador_envios_backend.domain.service.MotorRecomendacionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObtenerRecomendacionUseCaseImpl implements ObtenerRecomendacionUseCase {

    private final DistanciaClient distanciaClient;
    private final List<ProveedorClient> proveedores;
    private final MotorRecomendacionService motorRecomendacionService;

    public ObtenerRecomendacionUseCaseImpl(
            DistanciaClient distanciaClient,
            List<ProveedorClient> proveedores,
            MotorRecomendacionService motorRecomendacionService
    ) {
        this.distanciaClient = distanciaClient;
        this.proveedores = proveedores;
        this.motorRecomendacionService = motorRecomendacionService;
    }

    @Override
    public Recomendacion obtenerRecomendacion(Pedido pedido) {
        double distanciaKm = distanciaClient.obtenerDistanciaKm(pedido.origen(), pedido.destino());

        List<Cotizacion> cotizaciones = proveedores.stream()
                .map(proveedor -> proveedor.cotizar(pedido, distanciaKm))
                .toList();

        return motorRecomendacionService.recomendar(cotizaciones, pedido.prioridad());
    }
}
