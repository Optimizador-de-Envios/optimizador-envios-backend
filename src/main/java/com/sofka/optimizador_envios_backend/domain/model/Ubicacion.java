package com.sofka.optimizador_envios_backend.domain.model;

public record Ubicacion(
        String nombre,
        double lat,
        double lng
) {}
