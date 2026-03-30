package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto;

public record ConfirmacionPedidoResponseDto(
        String id,
        UbicacionDto origin,
        UbicacionDto destination,
        double weight,
        String weightUnit,
        String priority,
        double distanceKm,
        CotizacionDto selectedOption
) {}