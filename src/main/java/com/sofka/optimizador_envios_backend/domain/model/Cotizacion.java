package com.sofka.optimizador_envios_backend.domain.model;

public record Cotizacion(
        String nombreProveedor,
        double costo,
        int diasEntrega
) {}
