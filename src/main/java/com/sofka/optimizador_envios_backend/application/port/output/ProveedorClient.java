package com.sofka.optimizador_envios_backend.application.port.output;

import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;

public interface ProveedorClient {
    Cotizacion cotizar(Pedido pedido, double distanciaKm);
}
