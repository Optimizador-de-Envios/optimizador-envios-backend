package com.sofka.optimizador_envios_backend.application.port.input;

import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Recomendacion;

public interface ObtenerRecomendacionUseCase {
    Recomendacion obtenerRecomendacion(Pedido pedido);
}
