package com.sofka.optimizador_envios_backend.domain.model;

import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;

public record Pedido(
        Ubicacion origen,
        Ubicacion destino,
        double peso,
        UnidadPeso unidadPeso,
        Prioridad prioridad
) {}
