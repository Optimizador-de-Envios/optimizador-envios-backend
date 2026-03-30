package com.sofka.optimizador_envios_backend.application.usecase;

import com.sofka.optimizador_envios_backend.application.port.output.DistanciaClient;
import com.sofka.optimizador_envios_backend.application.port.output.ProveedorClient;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CotizarPedidoService {

    private final DistanciaClient distanciaClient;
    private final List<ProveedorClient> proveedores;

    public CotizarPedidoService(DistanciaClient distanciaClient, List<ProveedorClient> proveedores) {
        this.distanciaClient = distanciaClient;
        this.proveedores = proveedores;
    }

    public ResultadoCotizacionPedido cotizar(Pedido pedido) {
        double distanciaKm = distanciaClient.obtenerDistanciaKm(pedido.origen(), pedido.destino());

        List<Cotizacion> cotizaciones = proveedores.stream()
                .map(proveedor -> proveedor.cotizar(pedido, distanciaKm))
                .toList();

        return new ResultadoCotizacionPedido(distanciaKm, cotizaciones);
    }
}