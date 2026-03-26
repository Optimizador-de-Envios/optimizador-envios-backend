package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto;

public record CotizacionDto(String providerName, double cost, String currency, int estimatedDays) {}
