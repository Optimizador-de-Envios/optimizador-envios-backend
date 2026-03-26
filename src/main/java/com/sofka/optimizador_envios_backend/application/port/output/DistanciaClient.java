package com.sofka.optimizador_envios_backend.application.port.output;

import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;

public interface DistanciaClient {
    double obtenerDistanciaKm(Ubicacion origen, Ubicacion destino);
}
