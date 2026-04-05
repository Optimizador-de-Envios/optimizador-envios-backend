package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto;

import java.time.Instant;

public record PedidoHistorialResponseDto(
        String id,
        UbicacionDto origin,
        UbicacionDto destination,
        double weight,
        String weightUnit,
        String priority,
        double distanceKm,
        CotizacionDto selectedOption,
        Instant createdAt
) {}
