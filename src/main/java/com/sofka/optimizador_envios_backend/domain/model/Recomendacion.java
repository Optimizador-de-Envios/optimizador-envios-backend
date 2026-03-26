package com.sofka.optimizador_envios_backend.domain.model;

import java.util.List;

public record Recomendacion(
        Cotizacion recomendada,
        List<Cotizacion> alternativas
) {}
